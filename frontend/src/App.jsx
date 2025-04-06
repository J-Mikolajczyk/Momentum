import { use, useState } from 'react';
import SplashScreen from './components/SplashScreen';
import EmailForm from './components/EmailForm';

function App() {
  const [showEmailForm, setShowEmailForm] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);

  const handleShowEmailForm = () => {
    setShowEmailForm(true); 
  };

  return (
    <div>
      {showEmailForm ? (
        <EmailForm />
      ) : (
        <SplashScreen onClick={handleShowEmailForm} />
      )}
    </div>
  );
}

export default App;
