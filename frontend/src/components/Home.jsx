import React, { useState } from 'react';
import AddBlockPopup from '../components/AddBlockPopup'

function Home({ userInfo, setUserInfo }) {
  const[showSidebar, setShowSidebar] = useState(false);
  const[showAddBlockMenu, setshowAddBlockMenu] = useState(false);

  const toggleAddBlockMenu = () => {
    setshowAddBlockMenu(!showAddBlockMenu);
  }

    return (
      <div className='h-screen flex flex-col bg-white'>
        <nav className='flex justify-between bg-blue-800 pl-2.5 pr-4 pb-2'>
          <p className='select-none mt-3 text-white font-anton text-5xl'>MOMENTUM</p>
          <button className='select-none text-white font-anton text-6xl'>≡</button>
        </nav>
        <AddBlockPopup open={showAddBlockMenu} toggleAddBlockMenu={toggleAddBlockMenu} userInfo={userInfo} setUserInfo={setUserInfo}/>
        <div className='flex flex-col h-screen items-center pt-3 mx-3'>
          <button onClick={toggleAddBlockMenu} className='select-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 h-10 text-xl border-1 border-gray-500'>Add Block</button>   
        </div>
      </div>
    );
  }
  
  
  export default Home;
  