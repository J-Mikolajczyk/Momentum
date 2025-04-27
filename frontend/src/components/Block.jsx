import React, { useState, useEffect } from 'react';
import { getRequest } from '../utils/api';

export default function Block({ blockName, email }) {

  const ip = import.meta.env.VITE_IP_ADDRESS;

  const [blockData, setBlockData] = useState(null);
  const [weekNum, setWeekNum] = useState(null);

  const incrementWeek = () => {
    setWeekNum(weekNum+1);
  }

  const decrementWeek = () => {
    setWeekNum(weekNum-1);
  }

  const refresh = async () => {
    try {
      const refreshResponse = await getRequest(ip + '/block/get', { blockName, email });
      if (refreshResponse.ok) {
        const json = await refreshResponse.json();
        setBlockData(json);
        setWeekNum(1);
      } else {
        console.log('Non-OK response');
      }
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    refresh(); 
  }, []); 

    return <>
                <p className='text-blue-800 font-anton mr-auto text-3xl'>{blockName}</p>
                <div className='flex flex-row bg-gray-300 h-8 w-full justify-around items-center'>
                    <button onClick={decrementWeek} className='font-anton text-2xl'>&lt;</button>
                    <p className='font-anton text-lg'>Week {weekNum}</p>
                    <button onClick={incrementWeek} className='font-anton text-2xl'>&gt;</button>
                </div>
           </>
}