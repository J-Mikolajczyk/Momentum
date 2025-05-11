import React, { useEffect } from 'react';
import Day from './Day';
import MessagePopup from './MessagePopup';
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
    setDisplayWeekAndDay,
    addWeek,
    removeWeek,
    refreshBlock,
    addExerciseToDay,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
    setWeekAndDay,
  } = useBlock(blockName, userInfo);

  if (!userInfo?.id || !blockName) {
    return <div className="p-4 text-center">Please select a user and a block.</div>;
  }

  if (weekText === 'Loading...' || weekText === 'Loading...') {
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
      <MessagePopup message={message} setMessage={setMessage} ignoreMethod={ignoreMethod} />
      <div className="flex flex-row w-full flex-grow items-start gap-4 pb-8 px-4 pt-4">
        <div className="flex flex-col items-center rd w-full">
          <Day
              key={`${currentWeekNum}-${currentDayIndex}`} // Force re-render when indexes change
              blockData={blockData}
              currentWeekIndex={currentWeekNum}
              currentDayIndex={currentDayIndex}
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
