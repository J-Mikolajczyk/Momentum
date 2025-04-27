import { useEffect, useRef, useState } from 'react';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';
import Home from './components/Home'

function App() {
  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);

  const handleShowEmailForm = () => {
    setShowEmailForm(true); 
  };

  return (
    <div className='h-screen w-screen flex flex-col items-center justify-center bg-white'>
      { loggedIn ? 
        (<><Home userInfo={userInfo} setUserInfo={setUserInfo}/></>)
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
