import { useEffect } from 'react';

export default function useLockScroll(lock = true) {
  useEffect(() => {
    if (!lock) return;

    document.body.style.overflow = 'hidden';
    document.documentElement.style.overscrollBehavior = 'none';

    return () => {
      document.body.style.overflow = '';
      document.documentElement.style.overscrollBehavior = '';
    };
  }, [lock]);
}
