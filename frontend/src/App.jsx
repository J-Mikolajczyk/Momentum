import { useEffect } from 'react';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';
import Sidebar from './components/Sidebar';
import BlockDashboard from './components/BlockDashboard';
import Home from './components/Home';
import Navigation from './components/Navigation';
import setThemeColor from './hooks/useThemeColor'
import { useUser } from './contexts/UserContext';
import { AnimatePresence } from 'framer-motion';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function App() {

  const {
    loggedIn,
    showEmailForm,
    blockName,
  } = useUser();

  useEffect(() => {
    setThemeColor('#ffffff');
    setTimeout(() => {
      autoLogin();
    }, 1400);
  }, []);

  return (
    <div className='h-screen w-screen flex flex-col items-center justify-center bg-white'>
      {loggedIn ?
        <div className='h-full w-full flex flex-col bg-white'>
          <Navigation/>
          <Sidebar/>
          {blockName === null ? 
              <Home/>
            : <BlockDashboard />
          }
          </div> : 
        <div className='flex flex-col bg-white h-full w-full items-center justify-center overflow-hidden'>
          <SplashScreen/>
          {showEmailForm && (
            <EmailForm/>
           )}
        </div>
      }
    </div>
  );
}