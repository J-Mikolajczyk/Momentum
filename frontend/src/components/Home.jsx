import React, { useState, useRef, useEffect } from 'react';
import AddBlockPopup from '../components/AddBlockPopup';
import Sidebar from '../components/Sidebar';
import Block from '../components/Block';
import useLockScroll from '../hooks/useLockScroll';


function Home({ userInfo, setUserInfo }) {
  useLockScroll();
  
  const [showSidebar, setShowSidebar] = useState(false);
  const [showAddBlockMenu, setshowAddBlockMenu] = useState(false);
  const [blockName, setBlockName] = useState(null);

  const name = userInfo?.name != null ? userInfo.name : null;

  const toggleAddBlockMenu = () => {
    setshowAddBlockMenu(!showAddBlockMenu);
  };

  const goHome = () => {
    setBlockName(null);
  }

  const toggleSidebar = () => {
    setShowSidebar(!showSidebar);
  }

  const openBlock = (blockName) => {
    setBlockName(blockName);
  }

  return (
    <div className='h-screen flex flex-col bg-white'>
      <nav className='h-1/12 flex shrink-0 justify-between items-center bg-blue-800 pl-2.5'>
        <button onClick={goHome} className='select-none text-white font-anton text-5xl'>MOMENTUM</button>
        <button onClick={toggleSidebar} className='select-none text-white font-anton text-6xl pb-2 w-1/6'>≡</button>
      </nav>
      <Sidebar open={showSidebar} toggleSidebar={toggleSidebar} userInfo={userInfo} setUserInfo={setUserInfo}/>
      <div className='flex flex-col flex-grow items-center pt-3 mx-6 gap-2 pb-5 overflow-y-auto overscroll-contain'>
      { blockName === null ? 
        (<>
          <div className='flex w-full items-center mb-3'>
            <p className='text-blue-800 font-anton inline-block text-3xl'>Welcome, {name}</p>
            <button onClick={toggleAddBlockMenu} className='inline-block elect-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 h-10 text-xl border border-gray-500' > Add Block </button>
          </div>
          {userInfo?.trainingBlockNames?.length > 0 ? (
          userInfo.trainingBlockNames.map((blockName, index) => (
          <button onClick={() => openBlock(blockName)} key={index} className='bg-blue-50 text-blue-800 font-anton px-4 py-2 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1'>{blockName}</button> ))
          ) : (<p className='text-gray-500 font-anton text-2xl'>No Training Blocks Found</p>)}         
        </>) : (<>
          <Block blockName={blockName}/>
        </>)
      }
      </div> 
      <AddBlockPopup open={showAddBlockMenu} toggleAddBlockMenu={toggleAddBlockMenu} userInfo={userInfo} setUserInfo={setUserInfo}/>

      
    </div>
  );
}

export default Home;
