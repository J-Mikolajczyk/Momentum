import React, { useState, useEffect } from 'react';
import Day from '../models/Day';
import { postRequest, getRequest } from '../utils/api';
import setThemeColor from '../hooks/useThemeColor'

export default function RenameBlockPopup( {show, toggle, blockData, userInfo, update, index} ) {

    if (!show) {
        return <></>;
    }

    useEffect(() => {
        setThemeColor('#0D1E5C'); 
    }, []);

    const renameBlock = async (e) => {
      e.preventDefault();
      blockData.weeks[weekNum-1].days[index].name = blockDayName;
      update();
      handleClose();
    }

    const handleClose = () => {
        toggle();
        setThemeColor('#193cb8')
    }

    const handleOuterClick = () => {
      handleClose();
    };
  
    const handleInnerClick = (e) => {
      e.stopPropagation();
    };

    if (!open) {
        return <></>;
    }

    return (
        <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div onClick={handleInnerClick} className="bg-white p-5 pr-7 rounded-xl shadow-lg w-3/4 min-h-1/5">
            <div className='flex justify-between'>
            <p className='inline text-blue-900 font-anton text-2xl'>Rename Day</p>
            <button onClick={handleClose} className="font-anton inlinetext-gray-500 hover:text-gray-700 text-2xl ">X</button>
            </div>
            <form>
                <input className='mt-3 bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 text-2xl border-blue-900 border-2 pl-3' placeholder='Day Name' value={blockDayName} onChange={(e) => setBlockDayName(e.target.value)} required /> 
                <button type='submit' onClick={renameDay} className='mt-3 bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-2xl border-blue-900 border-2'>Add</button>
            </form>
            
          </div>
        </div>
      );
      
}