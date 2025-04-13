import React, { useState } from 'react';

function Home({ userInfo }) {

  const[showSidebar, setShowSidebar] = useState(false);

  var blocks = userInfo;

  console.log(blocks);


    return (
      <div className='h-screen flex flex-col bg-white'>
        <nav className='flex justify-between bg-blue-800 pl-2.5 pr-4 pb-2'>
          <p className='select-none mt-3 text-white font-anton text-5xl'>MOMENTUM</p>
          <button className='select-none text-white font-anton text-6xl'>≡</button>
        </nav>
        <div className='flex flex-col h-screen items-center pt-3'>
          <div className='border-blue-900 border-2 h-20 w-8/9 rounded-lg'>

          </div>
        </div>
      </div>
    );
  }
  
  
  export default Home;
  