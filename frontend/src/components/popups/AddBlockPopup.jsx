import React, { useState, useEffect, useSyncExternalStore } from 'react';
import { postRequest, getRequest } from '../../utils/api';
import { useUser } from '../../contexts/UserContext';
import { useUI } from '../../contexts/UIContext';

export default function AddBlockPopup( {} ) {

  const { fetchData, userInfo, logOut } = useUser();
  const { showAddBlockMenu, toggleAddBlockMenu } = useUI();

  const ip = import.meta.env.VITE_IP_ADDRESS;
    const [blockName, setBlockName] = useState('');
    const [message, setMessage] = useState('');

    const userId = userInfo?.id ? userInfo.id.toString() : null;
    const email = userInfo?.email != null ? userInfo.email : null;

    const [selectedDays, setSelectedDays] = useState([]);

    const handleDayToggle = (day) => {
      setSelectedDays((prevDays) =>
        prevDays?.includes(day)
          ? prevDays.filter((d) => d !== day)
          : [...prevDays, day]
      );
    };


    const handleClose = () => {
      setBlockName('');
      setMessage('');
      setSelectedDays([]);
      toggleAddBlockMenu();
    }

    const handleOuterClick = () => {
      handleClose();
    };
  
    const handleInnerClick = (e) => {
      e.stopPropagation();
    };

    const handleSubmit = async (e) => {
      e.preventDefault();
      if (selectedDays.length < 3) {
        setMessage('Please select at least three days.');
        return;
      }
      if (blockName === '' || blockName === null ) {
        setMessage('Block name is required.')
        return null;
      }
      
      const dayOrder = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
      const sortedDays = [...selectedDays].sort((a, b) => dayOrder.indexOf(a) - dayOrder.indexOf(b));


      const response = await postRequest(ip+'/secure/block/create', { blockName, userId, sortedDays });
      if (response.status === 201) {
        fetchData();
        handleClose();
      } else if (response.status === 409) {
        setMessage(blockName + ' already exists.');
      } else if (response.status === 403 || response.status === 401) {
        logOut();
      } else {
        setMessage('Error creating block')
      }
      return null;

    }


    if (!showAddBlockMenu) {
        return <></>;
    }

    return (
      <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
        <div onClick={handleInnerClick} className="flex flex-col justify-center bg-white p-5 rounded-xl shadow-lg w-7/8 min-h-1/4 max-w-150">
        <div className='flex justify-between'>
          <p className='inline text-blue-800 font-anton text-xl'>Add a Training Block</p>
          <button onClick={handleClose} className="font-anton inline text-gray-500 hover:text-gray-700 text-xl cursor-pointer">X</button>
        </div>
        <form>
          <input className='mt-1.5 bg-white font-anton rounded-md text-blue-800 h-1/4 w-full text-xl border-blue-800 border-2 pl-3' placeholder='Block Name' value={blockName} onChange={(e) => setBlockName(e.target.value)} required /> 
          <p className='text-blue-800 font-anton text-xl mt-1.5'>Select Training Days:</p>
            <div className='flex flex-row justify-around w-full'>
              {['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'].map((day) => {
                const isSelected = selectedDays?.includes(day);
                return (
                  <label
                    key={day}
                    onClick={() => handleDayToggle(day)}
                    className={`flex items-center justify-center w-1/8 pt-0.5 pr-0.5 text-sm font-anton rounded-md border-2 cursor-pointer transition duration-300
                      ${isSelected ? 'bg-blue-800 text-white border-blue-800' : 'bg-white text-blue-800 border-blue-800 hover:bg-gray-200'}`}
                  >
                    {day.substring(0,3).toUpperCase()}
                  </label>
                );
              })}
            </div>
          <button type='submit' onClick={handleSubmit} className='mt-3 bg-white font-anton rounded-md text-blue-800 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-xl border-blue-800 border-2 cursor-pointer'>Add</button>
          {message && (<p className='font-anton text-red-700 text-xl mt-2'>{message}</p> )}
        </form>
        </div>
      </div>
      );
      
}