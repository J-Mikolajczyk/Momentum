import { use, useState, useSyncExternalStore } from 'react';
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

  if(loggedIn) {
    return 
    <div className='h-screen overflow-hidden'>
    <Home userInfo={userInfo} setUserInfo={setUserInfo}/>;
    </div>
  }

  if (showEmailForm) {
    return
    <div className='h-screen overflow-hidden'>
     <EmailForm setLoggedIn={setLoggedIn} setUserInfo={setUserInfo}/>;
    </div>
  }

  return (
    <div className='h-screen overflow-hidden'>
     <SplashScreen onClick={handleShowEmailForm} />
     </div>
  );
}

export default App;
