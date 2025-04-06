import { useState } from 'react';
import SplashScreen from './components/SplashScreen';
import LoginRegister from './components/LoginRegister'; // import LoginRegister

function App() {
  const [showLoginRegister, setShowLoginRegister] = useState(false);

  const handleShowLoginRegister = () => {
    setShowLoginRegister(true); 
  };

  return (
    <div>
      {showLoginRegister ? (
        <LoginRegister />
      ) : (
        <SplashScreen onClick={handleShowLoginRegister} />
      )}
    </div>
  );
}

export default App;
