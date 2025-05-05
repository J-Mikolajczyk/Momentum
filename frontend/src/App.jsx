import { useEffect, useRef, useState } from 'react';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';
import Home from './components/Home'

function App() {
  const ip = import.meta.env.VITE_IP_ADDRESS;

  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);

  const handleShowEmailForm = () => {
    setShowEmailForm(true); 
  };

  useEffect(() => {
    
    const autoLogin = async () => {
      try {
        const response = await fetch(ip +'/auth/auto-login', {
          method: 'POST',
          credentials: 'include', // Include cookies in the request
        });
  
        if (response.ok) {
          const user = await response.json();
          setLoggedIn(true);
          setUserInfo(user);
        } else {
          console.log('Auto-login failed');
        }
      } catch (err) {
        console.error('Error during auto-login:', err);
      }
    };

    setTimeout(() => {
      autoLogin();
    } , 1400);
  }, []);

  return (
    <div className='h-screen w-screen flex flex-col items-center justify-center bg-white'>
      { loggedIn ? 
        (<><Home setLoggedIn={setLoggedIn} userInfo={userInfo} setUserInfo={setUserInfo}/></>)
        :(<div className='flex flex-col bg-white h-full w-full rounded-lg items-center justify-center'> 
          { showEmailForm ?
          (<><EmailForm setLoggedIn={setLoggedIn} setUserInfo={setUserInfo}/></>)
            :(<><SplashScreen onClick={handleShowEmailForm} /></>)} </div>
          )
      }
    </div>
  );
}

export default App;
