import { StrictMode, useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import './index.css';

function Root() {
  const [fontsLoaded, setFontsLoaded] = useState(false);

  useEffect(() => {
    document.fonts.load('1rem "Anton"').then(() => {
      setFontsLoaded(true);
    });
  }, []);

  if (!fontsLoaded) {
    return null;
  }

  return <App />;
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Root />
  </StrictMode>,
);