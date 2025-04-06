import React from 'react';

function LoginRegister() {
  return (
    <div className='h-screen flex flex-col items-center justify-center bg-linear-to-b from-blue-400 to-blue-900'>
      <h1 className="text-white font-anton text-5xl">Email:</h1>
      <input  className='bg-white font-anton rounded-md text-blue-900 py-3 px-10 text-2xl border-blue-900 border-2'></input>
      <button className='bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 py-3 px-10 text-4xl border-blue-900 border-2'   > Submit</button>
    </div>
  );
}

export default LoginRegister;
