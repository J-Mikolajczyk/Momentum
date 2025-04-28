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
                <div className='flex w-full items-center mb-3'>
                  <p className='text-blue-800 font-anton inline-block text-3xl'>{blockName}</p>
                  <button className='inline-block elect-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs' >Add Week</button>
                </div>
                <div className='flex flex-row bg-gray-300 h-8 w-full justify-around items-center'>
                    <button onClick={decrementWeek} className='font-anton text-2xl w-1/10'>&lt;</button>
                    <p className='font-anton text-lg'>Week {weekNum}</p>
                    <button onClick={incrementWeek} className='font-anton text-2xl w-1/10'>&gt;</button>
                </div>
                <div className='flex flex-col flex-grow items-center pt-3 mx-6 gap-2 pb-8'>
                  { blockData !== null ?
                    (<>{ blockData.weeks.length < 1 ? 
                      (<p className='text-gray-500 font-anton text-2xl'>No Weeks Created</p>)
                      : (<p>Non-0</p>)
                        }</>) : (<> </>)
                  }
                </div>
           </>
}