import React, { useState, useRef, useEffect } from 'react';
import AddBlockPopup from '../components/AddBlockPopup';
import Sidebar from '../components/Sidebar';
import BlockDashboard from './BlockDashboard';
import Navigation from './Navigation';
import setThemeColor from '../hooks/useThemeColor'
import {getRequest, postRequest} from '../utils/api'


function Home({ setLoggedIn, userInfo, setUserInfo }) {

  const ip = import.meta.env.VITE_IP_ADDRESS;

  const [showSidebar, setShowSidebar] = useState(false);
  const [showAddBlockMenu, setshowAddBlockMenu] = useState(false);
  const [blockName, setBlockName] = useState(null);
  const name = userInfo?.name != null ? userInfo.name : null;
  const userId = userInfo?.id != null ? userInfo.id : null;
  const email = userInfo?.email != null ? userInfo.email : null;

  const logOut = () => {
    logOutUser();
    setLoggedIn(false);
    setUserInfo(null);
    setThemeColor('#ffffff'); 
  }

  const logOutUser = async () => {
    try {
      const response = await postRequest(ip+'/auth/logout', {email});
      if (response.ok) {
        console.log('User logged out successfully.');
      } else {
        console.log('Error logging out user.');
      }
    } catch (err) {
      console.log(err);
    }
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
                  } else {
                    console.log('Response not OK');
                  }
                } catch (err) {
                  console.log(err);
         }
  }

  useEffect(() => {
    setThemeColor('#193cb8'); 
  }, []);
  
  
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
    fetchData();
  }

  const toggleSidebar = () => {
    setShowSidebar(!showSidebar);
  }

  

  return (
    <div className='h-full w-full flex flex-col bg-white'>
      <Navigation goHome={goHome} toggleSidebar={toggleSidebar}></Navigation>
      <Sidebar logOut={logOut} open={showSidebar} toggleSidebar={toggleSidebar} userInfo={userInfo} setUserInfo={setUserInfo}/>
      <div className='flex flex-col flex-grow items-center pt-3 mx-6 gap-2 pb-8'>
           <BlockDashboard blockName={blockName} setBlockName={setBlockName} userInfo={userInfo} toggleAddBlockMenu={toggleAddBlockMenu}/>
      </div> 
      
      <AddBlockPopup fetchData={fetchData} open={showAddBlockMenu} toggleAddBlockMenu={toggleAddBlockMenu} userInfo={userInfo} setUserInfo={setUserInfo}/>

      
    </div>
  );
}

export default Home;
