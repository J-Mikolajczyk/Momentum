import React, { useState, useEffect, useCallback } from 'react';
import { postRequest, getRequest } from '../utils/api';
import Week from '../models/Week';
import Exercise from '../models/Exercise';
import Set from '../models/Set';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function useBlock(blockName, userInfo) {
  const userId = userInfo?.id ?? null;

  const [blockData, setBlockData] = useState(null);
  const [currentWeekNum, setCurrentWeekNum] = useState(0);
  const [currentDayIndex, setCurrentDayIndex] = useState(0);
  const [weekText, setWeekText] = useState('Loading...');
  const [ignoreMethod, setIgnoreMethod] = useState(null);

  const computeWeekText = (data, weekNum, dayIndex) => {
    const day = data?.weeks?.[weekNum]?.days?.[dayIndex];
    const dayName = day?.name ?? '';
    return `Week ${weekNum + 1} Day ${dayIndex + 1} ${dayName}`;
  };

  

  useEffect(() => {
    fetchBlock();
  }, [fetchBlock]);

  const updateBlock = useCallback(async (updatedWeeks, weekIndex, dayIndex, options = {}) => {
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
        setBlockData(updated);
        setCurrentWeekNum(weekIndex);
        setCurrentDayIndex(dayIndex);
        setWeekText(computeWeekText(updated, weekIndex, dayIndex));
      }
    } catch (err) {
      console.error(err);
      setMessage('Error updating block.');
    }
  }, [blockData]);

  


  

  

  const deleteSetFromExercise = useCallback(async (exerciseName, setIndex, weekIndex, dayIndex) => {
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
  }, [blockData, updateBlock]);

  const updateSetData = useCallback(async (exerciseName, setIndex, field, value, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    const exercise = newWeeks[weekIndex]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
    if (exercise && exercise.sets[setIndex]) {
      exercise.sets[setIndex][field] = ['weight', 'reps'].includes(field) ? Number(value) || 0 : value;
    }
    await updateBlock(newWeeks, weekIndex, dayIndex);
  }, [blockData, updateBlock]);

  return {
    blockData,
    currentWeekNum,
    currentDayIndex,
    weekText,
    message,
    setMessage,
    ignoreMethod,
    setIgnoreMethod,
    setWeekAndDay,
    addWeek,
    removeWeek,
    addExerciseToDay,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
  };
}
