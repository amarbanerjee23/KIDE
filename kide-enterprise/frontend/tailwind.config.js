/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: '#1a1a2e',
        accent: '#0f3460',
        highlight: '#16c79a',
        surface: '#16213e'
      }
    },
  },
  plugins: [],
}

