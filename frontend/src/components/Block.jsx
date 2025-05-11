import React, { useEffect } from 'react';
import Day from './Day';
import MessagePopup from './MessagePopup';
import useBlock from '../hooks/useBlock';

export default function Block({ blockData, addExerciseToDay, addSetToExercise, deleteSetFromExercise, updateSetData }) {

  const currentWeekIndex = blockData?.mostRecentWeekOpen;
  const currentDayIndex = blockData?.mostRecentDayOpen;

  if (!blockData) {
    return <div className="p-4 text-center">Initializing...</div>;
  }

  return (
    <>
      <div className="flex flex-row w-full flex-grow items-start gap-4 pb-8 px-4 pt-4">
        <div className="flex flex-col items-center rd w-full">
          <Day
              blockData={blockData}
              currentWeekIndex={currentWeekIndex}
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
