import { useEffect } from 'react';

export default function usePreventBodyScroll() {
  useEffect(() => {
    const preventDefault = (e) => e.preventDefault();

    // Prevent scroll on the entire document
    document.body.style.overflow = 'hidden';

    // iOS fix - prevent rubber banding
    document.addEventListener('touchmove', preventDefault, { passive: false });

    return () => {
      document.body.style.overflow = '';
      document.removeEventListener('touchmove', preventDefault);
    };
  }, []);
}
