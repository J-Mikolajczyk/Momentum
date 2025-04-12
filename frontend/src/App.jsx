import { use, useState } from 'react';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';

function App() {
  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);

  const handleShowEmailForm = () => {
    setShowEmailForm(true); 
  };

  if(loggedIn) {
    return <Home />;
  }

  if (showEmailForm) {
    return <EmailForm setLoggedIn={setLoggedIn}/>;
  }

  return (
     <SplashScreen onClick={handleShowEmailForm} />
  );
}

export default App;
