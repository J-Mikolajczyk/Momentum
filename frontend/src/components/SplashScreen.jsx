import React, { useState, useRef, useEffect } from 'react';

function SplashScreen({ onClick }) {

  return (
    <>
      <h1 className="mt-10 text-white font-anton text-6xl text-shadow-lg">MOMENTUM</h1>
      <br></br>
      <button
        className='bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/5 w-4/5 text-4xl border-blue-900 border-2'
        onClick={onClick} 
      >
        LOGIN/REGISTER
      </button>
    </>
  );
}

export default SplashScreen;
