import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';
import AddBlockPopup from './components/AddBlockPopup';
import Sidebar from './components/Sidebar';
import BlockDashboard from './components/BlockDashboard';
import Home from './components/Home';
import Navigation from './components/Navigation';
import setThemeColor from './hooks/useThemeColor'
import {getRequest, loginRequest, logoutRequest} from './utils/api'
import { BlockDashboardContext } from './contexts/BlockDashboardContext';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function App() {

  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);

  const toggleShowEmailForm = () => {
    setShowEmailForm(!showEmailForm); 
  };

  const autoLogin = async () => {
      try {
        const response = await loginRequest(ip + '/auth/auto-login')

        if (response.ok) {
          setThemeColor('#193cb8');
          const user = await response.json();
          setLoggedIn(true);
          setUserInfo(user);
          if (user.trainingBlockNames.length >= 1) {
            setBlockName(user.trainingBlockNames[0]);
          }
        } else {
          toggleShowEmailForm();
        }
      } catch (err) {
        toggleShowEmailForm();
      }
  };

  useEffect(() => {
    setThemeColor('#ffffff');
    setTimeout(() => {
      autoLogin();
    }, 1400);
  }, []);

  const [showSidebar, setShowSidebar] = useState(false);
  const [showAddBlockMenu, setshowAddBlockMenu] = useState(false);
  const [blockName, setBlockName] = useState(null);
  const [weekText, setWeekText] = useState('Loading...');
  const [splashFinished, setSplashFinished] = useState(false);
  const userId = userInfo?.id != null ? userInfo.id : null;
  const email = userInfo?.email != null ? userInfo.email : null;

  const logOut = () => {
    logoutRequest(email);
    setLoggedIn(false);
    setShowEmailForm(true);
    setBlockName(null);
    setWeekText('Loading...');
    setUserInfo(null);
    setShowSidebar(false);
    setThemeColor('#ffffff'); 
  }

  const fetchData = async () => {
    try {
      const refreshResponse = await getRequest(ip+'/secure/user/refresh', { userId });
      if(refreshResponse.ok) {
        const json = await refreshResponse.json();
        if(json.exists === false) {
          console.log('User does not exist, redirecting to login');
          return;
        }
        setUserInfo(json);
      } else if (refreshResponse.status === 403 || refreshResponse.status === 401) {
        logOut();
        return;
      } else {
        console.log('Response not OK');
        return;
      }
    } catch (err) {
      console.log(err);
    }
  }
  
  const toggleAddBlockMenu = () => {
    setshowAddBlockMenu(!showAddBlockMenu);
    if (!showAddBlockMenu) {
      setThemeColor('#0D1E5C');
    } else {
      setThemeColor('#193cb8');
    }
  };

  const goHome = () => {
    setBlockName(null);
    setWeekText('Loading...');
    fetchData();
  }

  const toggleSidebar = () => {
    setShowSidebar(!showSidebar);
  }

  return (
    <div className='h-screen w-screen flex flex-col items-center justify-center bg-white'>
      {loggedIn ?
        <div className='h-full w-full flex flex-col bg-white'>
          <Navigation toggleSidebar={toggleSidebar}></Navigation>
          <Sidebar goHome={goHome} logOut={logOut} open={showSidebar} toggleSidebar={toggleSidebar} userInfo={userInfo} setUserInfo={setUserInfo}/>
          {blockName === null ? 
              <Home blockName={blockName} setBlockName={setBlockName} userInfo={userInfo} toggleAddBlockMenu={toggleAddBlockMenu} setWeekText={setWeekText} weekText={weekText} fetchData={fetchData} logOut={logOut}/>
            : <BlockDashboardContext.Provider value={{
                blockName,
                setBlockName,
                userInfo,
                toggleAddBlockMenu,
                setWeekText,
                weekText,
                fetchData,
                logOut
              }}>
                <BlockDashboard />
              </BlockDashboardContext.Provider>
          }
          <AddBlockPopup fetchData={fetchData} open={showAddBlockMenu} toggleAddBlockMenu={toggleAddBlockMenu} userInfo={userInfo} logOut={logOut}/>
        </div> : 
        <div className='flex flex-col bg-white h-full w-full items-center justify-center overflow-hidden'>
          <SplashScreen showEmailForm={showEmailForm}/>
          {showEmailForm && (
            <EmailForm setLoggedIn={setLoggedIn} setUserInfo={setUserInfo} />
           )}
        </div>
      }
    </div>
  );
}