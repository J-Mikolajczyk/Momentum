import React, { useEffect } from 'react';
import Day from './Day';

export default function Block({ currentWeekIndex, currentDayIndex, blockData, addExerciseToDay, addSetToExercise, deleteSetFromExercise, updateSetData }) {

  if (!blockData) {
    return;
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
