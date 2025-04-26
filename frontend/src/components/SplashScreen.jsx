import React, { useState, useRef, useEffect } from 'react';
import setThemeColor from '../hooks/useThemeColor'

function SplashScreen({ onClick }) {
  useEffect(() => {
      setThemeColor('#193cb8');
  }, []);

  return (
    <div className='h-screen flex flex-col items-center justify-center bg-blue-800 '>
      <h1 className="text-white font-anton text-6xl">MOMENTUM</h1>
      <button
        className='bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 py-3 px-10 text-4xl border-blue-900 border-2'
        onClick={onClick} 
      >
        LOGIN/REGISTER
      </button>
    </div>
  );
}

export default SplashScreen;
