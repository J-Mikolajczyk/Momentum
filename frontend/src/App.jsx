import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';
import Home from './components/Home';

function App() {
  const ip = import.meta.env.VITE_IP_ADDRESS;

  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [autoLoginFail, setAutoLoginFail] = useState(false);

  const toggleShowEmailForm = () => {
    setShowEmailForm(!showEmailForm); 
  };

  useEffect(() => {
    const autoLogin = async () => {
      try {
        const response = await fetch(ip + '/auth/auto-login', {
          method: 'POST',
          credentials: 'include',
        });

        if (response.ok) {
          const user = await response.json();
          setLoggedIn(true);
          setUserInfo(user);
        } else {
          toggleShowEmailForm();
          console.log('Auto-login failed');
        }
      } catch (err) {
        toggleShowEmailForm();
        console.error(err);
      }
    };

    setTimeout(() => {
      autoLogin();
    }, 1400);
  }, []);

  return (
    <div className='h-screen w-screen flex flex-col items-center justify-center bg-white'>
      {loggedIn ? (
        <Home setLoggedIn={setLoggedIn} userInfo={userInfo} setUserInfo={setUserInfo} toggleShowEmailForm={toggleShowEmailForm} />
      ) : (
        <div className='flex flex-col bg-white h-full w-full items-center justify-center'>
          <motion.div initial={{ y: 0 }} animate={showEmailForm ? { y: -100 } : { y: 0 }}transition={{ type: 'spring', stiffness: 100, damping: 15 }}>
            <SplashScreen />
          </motion.div>

          <AnimatePresence>
            {showEmailForm && (
              <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 0.8 }} className='w-full h-5/12 flex items-center justify-center' >
                <EmailForm setLoggedIn={setLoggedIn} setUserInfo={setUserInfo} />
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      )}
    </div>
  );
}

export default App;
