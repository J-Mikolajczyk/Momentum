import React, { useState } from 'react';
import PolicyPopup from './popups/PolicyPopup';
import { useUI } from '../contexts/UIContext.jsx';
import { useUser } from '../contexts/UserContext';

export default function Sidebar( {  } ) {

  const { logOut, setBlockName, setWeekText, fetchData } = useUser();

  const goHome = () => {
    setBlockName(null);
    setWeekText('Loading...');
    fetchData();
  }

  const { showSidebar, toggleSidebar } = useUI();

  const [type, setType] = useState(null);
  const handleOuterClick = () => {
    toggleSidebar();
  };
  
  const handleInnerClick = (e) => {
    e.stopPropagation();
  };

  const handleGoHome = async () => {
    goHome();
    toggleSidebar();
  }


  if (!showSidebar) {
    return null;
  }

    return (
      <>
        <div className="fixed bottom-0 left-0 right-0 z-5 bg-black/50 flex items-center justify-end bg-black/30 h-full" onClick={handleOuterClick}>
          <div className="flex flex-col gap-5 bg-white px-6 shadow-lg w-2/3 h-full" onClick={handleInnerClick}>
              <div className='h-1/12'></div>
              <button onClick={handleGoHome} className='bg-gray-400 text-gray-500 font-anton ml-auto w-3/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs cursor-pointer'>Home</button>
              <button onClick={logOut} className='bg-gray-400 text-gray-500 font-anton ml-auto w-3/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs cursor-pointer' >Log Out</button>
              <button onClick={() => setType('terms')} className='bg-gray-400 text-gray-500 font-anton ml-auto w-3/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs cursor-pointer' >Terms of Service</button>
              <button onClick={() => setType('privacy')} className='bg-gray-400 text-gray-500 font-anton ml-auto w-3/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs cursor-pointer' >Privacy Policy</button>
          </div>
        </div>
        <PolicyPopup type={type} setType={setType} location='sidebar'/>
      </>
    );
      
}