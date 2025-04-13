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
    return <Home userInfo={userInfo}/>;
  }

  if (showEmailForm) {
    return <EmailForm setLoggedIn={setLoggedIn} setUserInfo={setUserInfo}/>;
  }

  return (
     <SplashScreen onClick={handleShowEmailForm} />
  );
}

export default App;
