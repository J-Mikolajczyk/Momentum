import React, { useEffect, useState } from 'react';
import { postRequest, getRequest } from '../utils/api';
import Block from './Block';
import WeekMenu from './WeekMenu';
import MessagePopup from './MessagePopup';
import useBlock from '../hooks/useBlock';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function BlockDashboard({ blockName, setBlockName, userInfo, toggleAddBlockMenu }) {

  const [blockData, setBlockData] = useState(null);
  const [weekText, setWeekText] = useState('Loading...');
  const [currentWeekIndex, setCurrentWeekIndex] = useState(null);
  const [currentDayIndex, setCurrentDayIndex] = useState(null);
  const [ignoreMethod, setIgnoreMethod] = useState(null);

  const userId = userInfo?.id;

  useEffect(() => {
    if (blockName) fetchBlock();
  }, [blockName]);

  const computeWeekText = (data, weekNum, dayIndex) => {
    const day = data?.weeks?.[weekNum]?.days?.[dayIndex];
    const dayName = day?.name ?? '';
    return `Week ${weekNum + 1} Day ${dayIndex + 1} ${dayName}`;
  };

  const updateWeeks = async (newWeeks) => {
    updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  }

  const fetchBlock = async () => {
      if (!userId || !blockName) {
        setBlockData(null);
        setWeekText('Please select a block or user.');
        return;
      }
  
      try {
        const response = await getRequest(`${ip}/secure/block/get`, { blockName, userId });
        if (!response.ok) throw new Error('Failed to fetch block');
  
        const json = await response.json();
        if (json.exists === false) {
          setBlockData(null);
          setWeekText('Block not found.');
          return;
        }
  
        setBlockData(json);
        const { mostRecentWeekOpen, mostRecentDayOpen } = json;
        setCurrentWeekIndex(mostRecentWeekOpen);
        setCurrentDayIndex(mostRecentDayOpen);
        setWeekText(computeWeekText(json, mostRecentWeekOpen, mostRecentDayOpen));
      } catch (err) {
        console.error(err);
        setWeekText('Error loading block.');
      }
    };

    const updateBlock = async (updatedWeeks, weekIndex, dayIndex, options = {}) => {
        if (!blockData?.id || !blockData?.name) return;
    
        try {
          const response = await postRequest(`${ip}/secure/block/update`, {
            id: blockData.id,
            name: blockData.name,
            weeks: updatedWeeks,
            weekIndex,
            dayIndex
          });
    
          if (!response.ok) throw new Error('Failed to update block');
    
          if (!options.skipRefresh) {
            const updated = {
              ...blockData,
              weeks: updatedWeeks,
              mostRecentWeekOpen: weekIndex,
              mostRecentDayOpen: dayIndex
            };
            if(updatedWeeks.length === blockData.weeks.length-1) {
              setCurrentWeekIndex(weekIndex-1);
              setWeekText(computeWeekText(updated, weekIndex-1, dayIndex));
            } else {
              setCurrentWeekIndex(weekIndex);
              setWeekText(computeWeekText(updated, weekIndex, dayIndex));
            }
            setBlockData(updated);
            setCurrentDayIndex(dayIndex);
          }
        } catch (err) {
          console.error(err);
          setMessage('Error updating block.');
        }
    };

  const addExerciseToDay = async (exerciseName, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
      for (let i = weekIndex; i < newWeeks.length; i++) {
        const day = newWeeks[i].days?.[dayIndex];
        if (day) {
          day.exercises = day.exercises || [];
          day.exercises.push(new Exercise(exerciseName));
        }
      }
      await updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  };


  const setWeekAndDay = async (weekNum, dayNum) => {
      setCurrentWeekIndex(weekNum);
      setCurrentDayIndex(dayNum);
      setWeekText(computeWeekText(blockData, weekNum, dayNum));
      await updateBlock(blockData.weeks, weekNum, dayNum, { skipRefresh: true });
    };

  const addSetToExercise = async (exerciseName, weekIndex, dayIndex) => {
        const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
        for (let i = weekIndex; i < newWeeks.length; i++) {
          const exercise = newWeeks[i]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
          if (exercise) {
            exercise.sets = exercise.sets || [];
            exercise.sets.push(new Set());
          }
        }
        await updateBlock(newWeeks, weekIndex, dayIndex);
  };

  const deleteSetFromExercise = async (exerciseName, setIndex, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));

    const syncAcrossWeeks = (baseSetsLength) => {
      for (let i = weekIndex + 1; i < newWeeks.length; i++) {
        const exercise = newWeeks[i]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
        if (!exercise) continue;

        const diff = exercise.sets.length - baseSetsLength;
        if (diff > 0) exercise.sets.splice(baseSetsLength);
        else for (let j = 0; j < -diff; j++) exercise.sets.push({});
      }
    };

    for (let i = weekIndex; i < newWeeks.length; i++) {
      const exercises = newWeeks[i]?.days?.[dayIndex]?.exercises;
      const exIndex = exercises?.findIndex(ex => ex.name === exerciseName);
      if (exIndex !== -1) {
        const sets = exercises[exIndex].sets;
        sets.splice(setIndex, 1);
        if (sets.length === 0) exercises.splice(exIndex, 1);
        syncAcrossWeeks(sets.length);
        break;
      }
    }

    await updateBlock(newWeeks, weekIndex, dayIndex);
  };

  const updateSetData = async (exerciseName, setIndex, field, value, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    const exercise = newWeeks[weekIndex]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
    if (exercise && exercise.sets[setIndex]) {
      exercise.sets[setIndex][field] = ['weight', 'reps'].includes(field) ? Number(value) || 0 : value;
    }
    await updateBlock(newWeeks, weekIndex, dayIndex);
  };

  const name = userInfo.name;

  const openBlock = (blockName) => {
    setBlockName(blockName);
  };

  return (
    <>
      <MessagePopup/>
      {blockName === null ? (
        <div className="p-4 w-full">
          <div className="flex w-full items-center mb-3">
            <p className="text-blue-800 font-anton inline-block text-3xl">Welcome, {name}</p>
            <button
              onClick={toggleAddBlockMenu}
              className="inline-block elect-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs"
            >
              Add Block
            </button>
          </div>
          {userInfo?.trainingBlockNames?.length > 0 ? (
            userInfo.trainingBlockNames.map((blockName, index) => (
              <button
                onClick={() => openBlock(blockName)}
                key={index}
                className="bg-blue-50 text-blue-800 font-anton px-4 py-2 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1 mb-1"
              >
                {blockName}
              </button>
            ))
          ) : (
            <p className="text-gray-500 font-anton text-2xl">No Training Blocks Created</p>
          )}
        </div>
      ) : (
        <>
          <WeekMenu
            blockData={blockData}
            setWeekAndDay={setWeekAndDay}
            weekText={weekText}
            updateWeeks={updateWeeks}
          />
          <Block 
            blockData={blockData}
            addExerciseToDay={addExerciseToDay}
            addSetToExercise={addSetToExercise} 
            deleteSetFromExercise={deleteSetFromExercise}
            updateSetData={updateSetData}/>
        </>
      )}
    </>
  );
}
