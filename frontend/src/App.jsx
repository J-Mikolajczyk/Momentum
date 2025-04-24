import { useEffect, useRef, useState } from 'react';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';
import Home from './components/Home'
import useLockScroll from './hooks/useLockScroll';

function App() {
  useLockScroll();

  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);

  const handleShowEmailForm = () => {
    setShowEmailForm(true); 
  };

  return (
    <div className='h-full'>
      { loggedIn ? 
        (<><Home userInfo={userInfo} setUserInfo={setUserInfo}/></>)
        :(<> { showEmailForm ?
          (<><EmailForm setLoggedIn={setLoggedIn} setUserInfo={setUserInfo}/></>)
            :(<><SplashScreen onClick={handleShowEmailForm} /></>)}
        </>)
      }
    </div>
  );
}

export default App;
