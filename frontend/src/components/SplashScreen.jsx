import React, { useState, useRef, useEffect } from 'react';

function SplashScreen({ onClick }) {

  return (
    <div className='flex flex-col bg-white h-1/3 w-full rounded-lg items-center justify-around'>
      <h1 className="text-blue-800 font-anton text-7xl text-shadow-lg">MOMENTUM</h1>
      <button
        className='bg-white font-anton rounded-md text-blue-800 hover:bg-gray-200 transition duration-300 h-1/4 w-7/10 text-4xl border-blue-800 border-2'
        onClick={onClick} 
      >
        LOGIN/REGISTER
      </button>
    </div>
  );
}

export default SplashScreen;
