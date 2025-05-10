import React from 'react';

import WeekMenu from './WeekMenu';
import Day from './Day';
import MessagePopup from './MessagePopup'
import useBlock from '../hooks/useBlock';

export default function Block({ blockName, userInfo }) {
 
  const {
    blockData,
    currentWeekNum,
    currentDayIndex,
    weekText,
    message,
    setMessage,
    ignoreMethod,
    // setIgnoreMethod is handled by MessagePopup or within hook logic
    setDisplayWeekAndDay,
    addWeek,
    removeWeek,
    refreshBlock,
    addExerciseToDay,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
  } = useBlock(blockName, userInfo);

  if (!userInfo?.id || !blockName) {
    return <div className="p-4 text-center">Please select a user and a block.</div>;
  }

  if (weekText === 'Loading...' || weekText === 'Loading user or block info...') {
    return <div className="p-4 text-center">{weekText}</div>;
  }
  
  if (!blockData && (weekText === 'Block not found.' || weekText === 'Error loading block.')) {
    return <div className="p-4 text-center">{weekText} Consider creating it or selecting another.</div>;
  }

   if (!blockData) {
    return <div className="p-4 text-center">Initializing...</div>;
  }

  return (
    <>
      <MessagePopup message={message} setMessage={setMessage} ignoreMethod={ignoreMethod}/>

      <WeekMenu 
        blockData={blockData} 
        setDisplayWeekAndDay={setDisplayWeekAndDay} 
        weekText={weekText} 
        addWeek={addWeek} 
        removeWeek={removeWeek}
        // refreshBlock is used by WeekMenu for day clicks via setDisplayWeekAndDay
      />
      <div className="flex flex-row w-full flex-grow items-start gap-4 pb-8 px-4">
          <div className="flex flex-col items-center rd w-full">
            <Day 
              blockData={blockData} 
              currentDayIndex={currentDayIndex} 
              currentWeekNumIndex={currentWeekNum - 1} // Day component expects 0-indexed weekNum
              addExerciseToDay={addExerciseToDay}
              addSetToExercise={addSetToExercise}
              deleteSetFromExercise={deleteSetFromExercise}
              updateSetData={updateSetData}
             />
           </div>
       </div>
     </>
  );
}