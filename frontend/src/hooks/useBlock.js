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
  const [message, setMessage] = useState(null);
  const [ignoreMethod, setIgnoreMethod] = useState(null);

  const computeWeekText = (data, weekNum, dayIndex) => {
    const day = data?.weeks?.[weekNum]?.days?.[dayIndex];
    const dayName = day?.name ?? '';
    return `Week ${weekNum + 1} Day ${dayIndex + 1} ${dayName}`;
  };

  const fetchBlock = useCallback(async () => {
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
      setCurrentWeekNum(mostRecentWeekOpen);
      setCurrentDayIndex(mostRecentDayOpen);
      setWeekText(computeWeekText(json, mostRecentWeekOpen, mostRecentDayOpen));
    } catch (err) {
      console.error(err);
      setWeekText('Error loading block.');
    }
  }, [blockName, userId]);

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

  const setWeekAndDay = useCallback(async (weekNum, dayNum) => {

    setCurrentWeekNum(weekNum);
    setCurrentDayIndex(dayNum);
    setWeekText(computeWeekText(blockData, weekNum, dayNum));
    await updateBlock(blockData.weeks, weekNum, dayNum, { skipRefresh: true });
  }, [blockData, updateBlock]);

  const addWeek = useCallback(async () => {
    if (!blockData) return;

    if (blockData.weeks.length >= 6) {
      setMessage('Mesocycles longer than 6 weeks are not recommended.');
      setIgnoreMethod(() => () => proceedAddWeek());
      return;
    }
    proceedAddWeek();
  }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);

  const proceedAddWeek = useCallback(async () => {
    const newWeeks = [...blockData.weeks];
    const lastWeek = newWeeks[newWeeks.length - 1];
    newWeeks.push(new Week(lastWeek?.days || []));
    await updateBlock(newWeeks, currentWeekNum, currentDayIndex);
  }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);

  const removeWeek = useCallback(async () => {
    if (!blockData || blockData.weeks.length === 1) {
      setMessage('Cannot remove only week.');
      return;
    }

    if (blockData.weeks.length <= 4) {
      setMessage('Mesocycles under 4 weeks are not recommended.');
      setIgnoreMethod(() => () => proceedRemoveWeek());
      return;
    }
    proceedRemoveWeek();

    }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);

  const proceedRemoveWeek = useCallback(async () => {
    const newWeeks = blockData.weeks.slice(0, -1);
    await updateBlock(newWeeks, Math.min(currentWeekNum, newWeeks.length - 1), currentDayIndex);
  }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);

  const addExerciseToDay = useCallback(async (exerciseName, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    for (let i = weekIndex; i < newWeeks.length; i++) {
      const day = newWeeks[i].days?.[dayIndex];
      if (day) {
        day.exercises = day.exercises || [];
        day.exercises.push(new Exercise(exerciseName));
      }
    }
    console.log(currentWeekNum);
    await updateBlock(newWeeks, currentWeekNum, currentDayIndex);
  }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);

  const addSetToExercise = useCallback(async (exerciseName, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    for (let i = weekIndex; i < newWeeks.length; i++) {
      const exercise = newWeeks[i]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
      if (exercise) {
        exercise.sets = exercise.sets || [];
        exercise.sets.push(new Set());
      }
    }
    await updateBlock(newWeeks, weekIndex, dayIndex);
  }, [blockData, updateBlock]);

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
