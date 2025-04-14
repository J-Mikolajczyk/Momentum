import React, { useState } from 'react';

function Home({ userInfo }) {
  const[showSidebar, setShowSidebar] = useState(false);
  const[showAddBlockMenu, setshowAddBlockMenu] = useState(false);

  const toggleAddBlockMenu = () => {
    setshowAddBlockMenu(!showAddBlockMenu);
  }

  var blocks = userInfo;

  console.log(blocks);


    return (
      <div className='h-screen flex flex-col bg-white'>
        <nav className='flex justify-between bg-blue-800 pl-2.5 pr-4 pb-2'>
          <p className='select-none mt-3 text-white font-anton text-5xl'>MOMENTUM</p>
          <button className='select-none text-white font-anton text-6xl'>≡</button>
        </nav>
        <div className='flex flex-col h-screen items-center pt-3 mx-3'>
          <button onClick={toggleAddBlockMenu} className='select-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 h-10 text-xl border-1 border-gray-500'>Add Block</button>
          <div className='border-blue-900 border-2 h-20 w-full rounded-lg'>

          </div>
        </div>
      </div>
    );
  }
  
  
  export default Home;
  