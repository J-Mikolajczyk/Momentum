import React, { useState, useRef, useEffect } from 'react';
import AddBlockPopup from '../components/AddBlockPopup';
import Sidebar from '../components/Sidebar';
import Block from '../components/Block';
import setThemeColor from '../hooks/useThemeColor'


function Home({ userInfo, setUserInfo }) {

  useEffect(() => {
    setThemeColor('#193cb8');
  }, []);
  
  
  const [showSidebar, setShowSidebar] = useState(false);
  const [showAddBlockMenu, setshowAddBlockMenu] = useState(false);
  const [blockName, setBlockName] = useState(null);
  const name = userInfo?.name != null ? userInfo.name : null;
  const email = userInfo?.email != null ? userInfo.email : null;

  const toggleAddBlockMenu = () => {
    setshowAddBlockMenu(!showAddBlockMenu);
    if (!showAddBlockMenu) {
      setThemeColor('#0D1E5C');
    } else {
      setThemeColor('#193cb8');
    }
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
    <div className='h-full w-full flex flex-col bg-white'>
      <nav className='bg-blue-800 sticky top-0 z-10 h-1/12 flex shrink-0 justify-between items-center pl-2.5'>
        <button onClick={goHome} className='select-none text-white font-anton text-5xl'>MOMENTUM</button>
        <button onClick={toggleSidebar} className='select-none text-white font-anton text-6xl pb-2 w-1/6'>≡</button>
      </nav>
      <div className='h-1/12 w-full'></div>
      <Sidebar open={showSidebar} toggleSidebar={toggleSidebar} userInfo={userInfo} setUserInfo={setUserInfo}/>
      <div className='flex flex-col flex-grow items-center pt-3 mx-6 gap-2 pb-8'>
      { blockName === null ? 
        (<>{userInfo?.trainingBlockNames?.length > 0 ? (
          userInfo.trainingBlockNames.map((blockName, index) => (
          <button onClick={() => openBlock(blockName)} key={index} className='bg-blue-50 text-blue-800 font-anton px-4 py-2 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1'>{blockName}</button> ))
          ) : (<p className='text-gray-500 font-anton text-2xl'>No Training Blocks Created</p>)}         
        </>) : (<>
          <Block blockName={blockName} email={email}/>
        </>)
      }
      </div> 
      <AddBlockPopup open={showAddBlockMenu} toggleAddBlockMenu={toggleAddBlockMenu} userInfo={userInfo} setUserInfo={setUserInfo}/>

      
    </div>
  );
}

export default Home;
