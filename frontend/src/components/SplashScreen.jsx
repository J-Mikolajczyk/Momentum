import React, { useState, useEffect } from 'react';

function SplashScreen({ onClick }) {
  const [showButton, setShowButton] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setShowButton(true), 1500);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className='flex flex-col bg-white h-1/8 w-full rounded-lg items-center justify-around'>
      <h1 className="text-blue-800 font-anton-italic text-7xl text-shadow-lg">MOMENTUM</h1>
    </div>
  );
}

export default SplashScreen;
