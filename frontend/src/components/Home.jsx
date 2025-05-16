import React, { useEffect, useState, useRef } from 'react';
import { postRequest, getRequest } from '../utils/api';
import RenameBlockPopup from './RenameBlockPopup';
import DeleteBlockPopup from './DeleteBlockPopup';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function Home({ fetchData, setWeekText, blockName, setBlockName, userInfo, toggleAddBlockMenu, logOut }) {

  const [blockData, setBlockData] = useState(null);
  const [currentWeekIndex, setCurrentWeekIndex] = useState(null);
  const [currentDayIndex, setCurrentDayIndex] = useState(null);
  const [ignoreMethod, setIgnoreMethod] = useState(null);
  const [activeMenuIndex, setActiveMenuIndex] = useState(null);
  const [showRenamePopup, setShowRenamePopup] = useState(false);
  const [showDeletePopup, setShowDeletePopup] = useState(false);
  const [renameBlockName, setRenameBlockName] = useState('');
  const [deleteBlockName, setDeleteBlockName] = useState('');
  const menuRef = useRef(null); 

  const userId = userInfo?.id;

  const handleActiveMenu = (index) => {
    if (index === activeMenuIndex) {
      setActiveMenuIndex(null);
      return;
    }
    setActiveMenuIndex(index);
    
  }

  useEffect(() => {
    if (blockName) {
      fetchBlock();
    } else {
      setBlockData(null);
    }
  }, [blockName]);

  const computeWeekText = (data, weekNum, dayIndex) => {
    const day = data?.weeks?.[weekNum]?.days?.[dayIndex];
    const dayName = day?.name ?? '';
    return `Week ${weekNum + 1} Day ${dayIndex + 1} ${dayName}`;
  };

  const fetchBlock = async () => {
      if (!userId || !blockName) {
        setBlockData(null);
        setWeekText('Please select a block or user.');
        return;
      }
  
      try {
        const response = await getRequest(`${ip}/secure/block/get`, { blockName, userId });
        if (response.status === 403 || response.status === 401) {
            logOut();
            return;
        }

        if (!response.ok) throw new Error('Failed to update block');
  
        const json = await response.json();
        if (json.exists === false) {
          setBlockData(null);
          setWeekText('Block not found.');
          return;
        }
  
        setBlockData(json);
        const { mostRecentWeekOpen, mostRecentDayOpen } = json;
        setCurrentWeekIndex(mostRecentWeekOpen);
        setCurrentDayIndex(mostRecentDayOpen);
        setWeekText(computeWeekText(json, mostRecentWeekOpen, mostRecentDayOpen));
      } catch (err) {
        console.error(err);
        setWeekText('Error loading block.');
      }
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setActiveMenuIndex(null);
      }
    };
    if (activeMenuIndex !== null) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [activeMenuIndex]);

    const handleDeleteBlock = async(blockName) => {
      setShowDeletePopup(true);
      setDeleteBlockName(blockName);
      setActiveMenuIndex(null);
    };


    const deleteBlock = async (blockName) => {
  
      try {
        const response = await postRequest(`${ip}/secure/block/delete`, { blockName, userId });
        if (response.status === 403 || response.status === 401) {
            logOut();
            return;
        }

        if (!response.ok) throw new Error('Failed to update block');
  
      } catch (err) {
        console.error(err);
      }
      setActiveMenuIndex(null);
      fetchData();
    };


  const renameBlock = async (blockName, newName) => {
    try {
        const response = await postRequest(`${ip}/secure/block/rename`, { blockName, newName, userId });
        if (response.status === 403 || response.status === 401) {
            logOut();
            return;
        }

        if (!response.ok) throw new Error('Failed to update block');
  
      } catch (err) {
        console.error(err);
      }
      setActiveMenuIndex(null);
      fetchData();
    };

  const handleRenameBlock = async (blockName) => {
    setShowRenamePopup(true);
    setRenameBlockName(blockName);
    setActiveMenuIndex(null);
  };

  const name = userInfo.name;

  const openBlock = (blockName) => {
    setBlockName(blockName);
    setActiveMenuIndex(null);
  };

  return (
      <>
        <div className="p-4 w-full overflow-y-auto scrollbar-hide min-h-[100dvh] pb-25" >
          <div className="flex w-full items-center mb-3">
            <p className="text-blue-800 font-anton inline-block text-3xl">Welcome, {name}</p>
            <button onClick={toggleAddBlockMenu} className="inline-block elect-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs cursor-pointer">Add Block</button>
          </div>
          {userInfo?.trainingBlockNames?.length > 0 ? (
            userInfo.trainingBlockNames.map((blockName, index) => (
              <div ref={menuRef} key={index} className="flex flex-row items-center justify-between bg-blue-50 text-blue-800 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1 mb-1">
                <button onClick={() => openBlock(blockName)} className="font-anton text-blue-800 w-9/10 text-left px-2 py-2 cursor-pointer">{blockName}</button>
                <button onClick={() => handleActiveMenu(index)} className="text-blue-800 font-anton-bold text-2xl px-4 py-2 text-right w-1/10 relative cursor-pointer">⫶</button>
                {activeMenuIndex === index && (
                        <div className="absolute right-10 mt-25 w-32 bg-white border border-gray-300 rounded shadow-md z-10">
                            
                            <button onClick={() => handleRenameBlock(blockName)} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 text-md cursor-pointer">Rename</button>
                            <button onClick={() => handleDeleteBlock(blockName)} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 text-md cursor-pointer">Delete</button>
                        </div>
                )}
              </div>
            ))
          ) : (
            <p className="text-gray-500 font-anton text-2xl">No Training Blocks Created</p>
          )}
        </div>
        <RenameBlockPopup show={showRenamePopup} toggle={() => setShowRenamePopup()} name={renameBlockName} rename={renameBlock} logOut={logOut} />
        <DeleteBlockPopup show={showDeletePopup} toggle={() => setShowDeletePopup()} name={deleteBlockName} deleteMethod={deleteBlock} logOut={logOut} />
    </>
  );
}
