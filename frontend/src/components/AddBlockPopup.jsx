import React, { useState, useEffect, useSyncExternalStore } from 'react';
import { postRequest, getRequest } from '../utils/api';

export default function AddBlockPopup( {fetchData, open, toggleAddBlockMenu, userInfo, setUserInfo} ) {

  const ip = import.meta.env.VITE_IP_ADDRESS;
    const [blockName, setBlockName] = useState('');
    const [message, setMessage] = useState('');

    const userId = userInfo?.id ? userInfo.id.toString() : null;
    const email = userInfo?.email != null ? userInfo.email : null;

    const [selectedOption, setSelectedOption] = useState(null);
    const [dayAmount, setDayAmount] = useState(null);

    const handleOptionClick = (option) => {
      setSelectedOption(option);
      setDayAmount(option);
    };

    const handleClose = () => {
      setBlockName('');
      setMessage('');
      setDayAmount(null);
      setSelectedOption(null);
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
      if (!selectedOption) {
        setMessage('Please select a day amount.');
        return;
      }
      if (blockName === '' || blockName === null ) {
        setMessage('Block name is required.')
        return null;
      }
      

      try {
              const response = await postRequest(ip+'/secure/block/create', { blockName, userId, dayAmount });
              console.log(response);
              if (response.status === 201) {
                fetchData();
                handleClose();
                return null;
              } else if (response.status === 409) {
                setMessage(blockName + ' already exists.');
              } else {
                setMessage('Error creating block')
                return null;
              }
            } catch (err) {
              setMessage('Error creating block')
              return null;
            }
    }


    if (!open) {
        return <></>;
    }

    return (
      <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
        <div onClick={handleInnerClick} className="bg-white p-5 pr-7 rounded-xl shadow-lg w-3/4 min-h-1/4">
        <div className='flex justify-between'>
          <p className='inline text-blue-800 font-anton text-xl'>Add a Training Block</p>
          <button onClick={handleClose} className="font-anton inline text-gray-500 hover:text-gray-700 text-xl">X</button>
        </div>
        <form>
          <input className='mt-1.5 bg-white font-anton rounded-md text-blue-800 h-1/4 w-6/7 text-xl border-blue-800 border-2 pl-3' placeholder='Block Name' value={blockName} onChange={(e) => setBlockName(e.target.value)} required /> 
          <p className=' text-blue-800 font-anton text-xl mt-1.5'>Weekly Training Days:</p>
          <div className='flex flex-row mt-1.5 space-x-4'>
            
            {[3, 4, 5, 6].map((option) => {
              const isSelected = selectedOption === option;
              return (
                <label key={option} onClick={() => handleOptionClick(option)} className={`pr-0.5 flex items-center justify-center font-anton rounded-md border-2 w-1/5 cursor-pointer transition duration-300
                    ${isSelected ? 'bg-blue-800 text-white border-blue-800' : 'bg-white text-blue-800 border-blue-800 hover:bg-gray-200'}`}
                >
                <input type="radio" name="blockOption" value={option} className="hidden"/> {option}</label>
              );
            })}
          </div>

          <button type='submit' onClick={handleSubmit} className='mt-3 bg-white font-anton rounded-md text-blue-800 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-xl border-blue-800 border-2'>Add</button>
          {message && (<p className='font-anton text-red-700 text-xl mt-2'>{message}</p> )}
        </form>
        </div>
      </div>
      );
      
}