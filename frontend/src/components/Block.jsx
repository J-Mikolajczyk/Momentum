import React, { useState, useEffect } from 'react';
import { postRequest, getRequest } from '../utils/api';
import AddDayPopup from './AddDayPopup';
import Day from './Day';
import Week from '../models/Week';

export default function Block({ blockName, userInfo }) {

  const ip = import.meta.env.VITE_IP_ADDRESS;

  const email = userInfo?.email != null ? userInfo.email : null;
  const userId = userInfo?.id != null ? userInfo.id : null;

  const [blockData, setBlockData] = useState(null);
  const [weekNum, setWeekNum] = useState(0);
  const [showAddDayPopup, setShowAddDayPopup] = useState(false);
  const [showDay, setShowDay] = useState(false);
  const [dayIndex, setDayIndex] = useState(0);

  const toggleDay = () => {
    setShowDay(!showDay);
  }

  const openDay = (index) => {
    setDayIndex(index);
    toggleDay();
  };

  const toggleAddDayPopup = () => {
    setShowAddDayPopup(!showAddDayPopup);
  }

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
    if (blockData.weeks.length === 0 || blockData.weeks[weekNum-1].length === 0 ) {
      blockData.weeks.push(new Week());
    } else {
      blockData.weeks.push(new Week(blockData.weeks[weekNum-1].days))
    }
    
    update();
  }



  const update = async () => {
    const id = blockData.id;
    const name = blockName;
    const weeks = blockData.weeks;
    try {
       const updateResponse = await postRequest(ip + '/secure/block/update', { name, id, weeks });
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
      const refreshResponse = await getRequest(ip + '/secure/block/get', { blockName, userId });
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

  return (
    <>
      {showDay ? 
        (<Day blockData={blockData} dayNum={dayIndex} weekNum={weekNum - 1} toggleDay={toggleDay} update={update} />) : 
        (<>
          <div className="flex flex-row w-full justify-between items-center mb-3">
            <p className="text-blue-800 font-anton inline-block text-3xl">{blockName}</p>
            <button onClick={addWeek} className="inline-block elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs">Add Week</button>
          </div>
          <div className="flex flex-row bg-gray-300 h-8 w-full justify-around items-center">
            {weekNum < 2 ?
              (<button className="font-anton text-gray-300 text-2xl w-1/10">&lt;</button>) : 
              (<button onClick={decrementWeek} className="font-anton text-2xl w-1/10">&lt;</button>)
            }
            <p className="font-anton text-lg">{weekText}</p>
            {!blockData || !blockData.weeks || weekNum === blockData.weeks.length || blockData.weeks.length === 0 ? 
              (<button className="font-anton text-gray-300 text-2xl w-1/10">&gt;</button>) : 
              (<button onClick={incrementWeek} className="font-anton text-2xl w-1/10">&gt;</button>)
            }
          </div>
          <div className="flex flex-col w-full flex-grow items-center gap-2 pb-8">
          {blockData?.weeks?.length > 0 && (
              <>
                {blockData.weeks[weekNum - 1]?.days?.length > 0 ? (
                  blockData.weeks[weekNum - 1].days.map((day, index) => (
                    <button
                      onClick={() => openDay(index)}
                      key={index}
                      className="bg-blue-50 text-blue-800 font-anton px-4 py-2 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1"
                    >
                      {day.name}
                    </button>
                  ))
                ) : (
                  <div className="flex flex-row w-full items-center justify-between">
                    <p className="text-gray-500 font-anton text-2xl">No Days Created</p>
                  </div>
                )}

              <button onClick={toggleAddDayPopup} className="flex elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-6 text-l border items-center justify-center border-gray-500 rounded-xs ml-auto">Add Day</button>
              </>
            )}

            <AddDayPopup show={showAddDayPopup} toggle={toggleAddDayPopup} blockData={blockData} setBlockData={setBlockData} blockName={blockName} weekNum={weekNum} update={update}/>
          </div>
        </>
      )}
    </>
  );
}