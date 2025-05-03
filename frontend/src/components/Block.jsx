import React, { useState, useEffect } from 'react';
import { postRequest, getRequest } from '../utils/api';
import Week from '../models/Week';

export default function Block({ blockName, userInfo }) {

  const ip = import.meta.env.VITE_IP_ADDRESS;

  const email = userInfo?.email != null ? userInfo.email : null;
  const userId = userInfo?.id != null ? userInfo.id : null;

  const [blockData, setBlockData] = useState(null);
  const [weekNum, setWeekNum] = useState(0);

  const setWeekNameAndNum = (weekNum) => {
    setWeekNum(weekNum);
    setWeekText("Week " + (weekNum));
  }

  const [weekText, setWeekText] = useState('Loading...');

  const incrementWeek = () => {
    setWeekNameAndNum(weekNum+1);
  }

  const decrementWeek = () => {
    setWeekNameAndNum(weekNum-1);
  }

  const addWeek = async () => {
    blockData.weeks.push(new Week([]));
    update();
  }

  const update = async () => {
    const id = blockData.id;
    const name = blockName;
    const weeks = blockData.weeks;
    try {
      const updateResponse = await postRequest(ip + '/block/update', { name, id, weeks });
      if (updateResponse.ok) {
        refresh();
      } else {
        console.log('Issue updating block');
      }
    } catch (err) {
      console.log(err);
    }
  }

  const refresh = async () => {
    try {
      const refreshResponse = await getRequest(ip + '/block/get', { blockName, userId });
      if (refreshResponse.ok) {
        const json = await refreshResponse.json();
        if (json.exists === false) {
          console.log('Issue with block data, block does not exist');
          return;
        }
        if (json.weeks.length > 0) {
          if(weekNum === 0) {
            setWeekNameAndNum(1);
          } else if (weekNum === json.weeks.length-1) {
            setWeekNameAndNum(weekNum+1);
          }
        } else {
          setWeekText('No Weeks Created');
        }
        setBlockData(json);
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
                <div className='flex flex-row w-full justify-between items-center mb-3'>
                  <p className='text-blue-800 font-anton inline-block text-3xl'>{blockName}</p>
                  <button onClick={addWeek} className='inline-block elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs' >Add Week</button>
                </div>
                <div className='flex flex-row bg-gray-300 h-8 w-full justify-around items-center'>
                    { weekNum < 2 ? (<button className='font-anton text-gray-300 text-2xl w-1/10'>&lt;</button>) 
                    : (<><button onClick={decrementWeek} className='font-anton text-2xl w-1/10'>&lt;</button></>)
                    }              
                    <p className='font-anton text-lg'>{weekText}</p>
                    { weekNum === blockData?.weeks?.length ? (<button className='font-anton text-gray-300 text-2xl w-1/10'>&lt;</button>) 
                    : (<><button onClick={incrementWeek} className='font-anton text-2xl w-1/10'>&gt;</button></>)
                    } 
                </div>
                <div className='flex flex-col w-full flex-grow items-center pt-3 gap-2 pb-8'>
                  {blockData !== null ? (
                    <>
                      {blockData.weeks.length < 1 ? (
                        <></>
                      ) : ( <> {blockData?.weeks[weekNum-1]?.days === null || blockData?.weeks[weekNum-1]?.days?.length === 0 ? 
                                  (<div className='flex flex-row w-full items-center justify-between'>
                                    <p className='text-gray-500 font-anton text-2xl'>No Days Created</p>
                                    <button className='inline-block elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs' >Add Day</button>
                                  </div>) : 
                                  ('Days Found')}
                            </>
                      )}
                    </>
                  ) : (
                    <></>
                  )}
                  <button onClick={update} className='inline-block elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs' >Save</button>
                </div>
           </>
}