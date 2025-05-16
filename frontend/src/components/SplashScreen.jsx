import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';

function SplashScreen({ showEmailForm }) {
  const [showButton, setShowButton] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setShowButton(true), 1500);
    return () => clearTimeout(timer);
  }, []);

  return (
    <motion.div className='absolute h-1/8 w-full flex items-center justify-center' initial={{ y: 0 }} animate={showEmailForm ? { y: -200 } : { y: 0 }}transition={{ type: 'spring', stiffness: 100, damping: 15 }}>
      <h1 className="text-blue-800 font-anton-italic text-7xl text-shadow-lg">MOMENTUM</h1>
    </motion.div>
  );
}

export default SplashScreen;
