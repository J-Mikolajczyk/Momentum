export function enableScrollOnElement(el) {
    if (!el) return;
  
    el.addEventListener('touchstart', () => {
      const top = el.scrollTop;
      const totalScroll = el.scrollHeight;
      const currentScroll = top + el.offsetHeight;
  
      if (top === 0) {
        el.scrollTop = 1;
      } else if (currentScroll === totalScroll) {
        el.scrollTop = top - 1;
      }
    });
  
    el.addEventListener(
      'touchmove',
      (e) => {
        if (el.offsetHeight < el.scrollHeight) {
          e.stopPropagation();
        }
      },
      { passive: false }
    );
  }
  