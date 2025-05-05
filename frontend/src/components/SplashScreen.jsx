import React, { useState, useEffect } from 'react';

function SplashScreen({ onClick }) {
  const [showButton, setShowButton] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setShowButton(true), 1500);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className='flex flex-col bg-white h-1/3 w-full rounded-lg items-center justify-around'>
      <h1 className="text-blue-800 font-anton text-7xl text-shadow-lg">MOMENTUM</h1>
      <button  onClick={onClick} className={` bg-white font-anton rounded-md text-blue-800 hover:bg-gray-200 transition duration-300 h-1/4 w-7/10 text-4xl border-blue-800 border-2 transform transition-opacity duration-700 ease-in-out ${showButton ? 'opacity-100' : 'opacity-0 pointer-events-none'} `} > LOGIN/REGISTER </button>
    </div>
  );
}

export default SplashScreen;
