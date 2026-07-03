/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./index.html'],
  // Classes aplicadas dinamicamente via JS (status do formulário de contato)
  safelist: ['text-emerald-400', 'text-red-400'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Montserrat', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
