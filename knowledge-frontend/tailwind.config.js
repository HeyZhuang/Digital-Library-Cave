/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // 数字智库色彩系统
        deepSpace: {
          50: '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a', // 主色调-深空灰
        },
        gold: {
          50: '#fefce8',
          100: '#fef9c3',
          200: '#fef08a',
          300: '#fde047',
          400: '#facc15',
          500: '#eab308',
          600: '#d4af37', // 主色调-鎏金
          700: '#a16207',
          800: '#854d0e',
          900: '#713f12',
        },
        parchment: {
          50: '#fefef9',
          100: '#fefcf3',
          200: '#fdf8e7',
          300: '#fcf2db',
          400: '#faecc3',
          500: '#f8f5f0', // 主色调-羊皮纸白
          600: '#f5f0e5',
          700: '#e8dcc4',
          800: '#d4c4a0',
          900: '#b8a57c',
        },
        // 保留原有的功能色彩
        success: {
          50: '#f0fdf4',
          100: '#dcfce7',
          500: '#22c55e',
          600: '#16a34a',
          700: '#15803d',
        },
        warning: {
          50: '#fffbeb',
          100: '#fef3c7',
          500: '#f59e0b',
          600: '#d97706',
          700: '#b45309',
        },
        error: {
          50: '#fef2f2',
          100: '#fee2e2',
          500: '#ef4444',
          600: '#dc2626',
          700: '#b91c1c',
        },
        // 更新主题色彩
        primary: {
          50: '#fefce8',
          100: '#fef9c3',
          200: '#fef08a',
          300: '#fde047',
          400: '#facc15',
          500: '#eab308',
          600: '#d4af37',
          700: '#a16207',
          800: '#854d0e',
          900: '#713f12',
        },
        secondary: {
          50: '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
        },
        title: '#0f172a',
        background: '#f8f5f0',
        // 高对比度灰色系统
        gray: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          400: '#9ca3af',
          500: '#6b7280', // 比默认的更深，确保对比度
          600: '#4b5563', // 比默认的更深，确保对比度
          700: '#374151', // 比默认的更深，确保对比度
          800: '#1f2937',
          900: '#111827',
        },
      },
      fontFamily: {
        // 字体组合：标题-思源宋体 / 正文-霞鹜文楷 / 代码-JetBrains Mono
        serif: ['Source Han Serif SC', 'Source Han Serif', 'Noto Serif CJK SC', 'serif'],
        sans: ['LXGW WenKai', 'Source Han Sans SC', 'Noto Sans CJK SC', 'sans-serif'],
        mono: ['JetBrains Mono', 'SF Mono', 'Monaco', 'Consolas', 'monospace'],
        title: ['Source Han Serif SC', 'Source Han Serif', 'serif'],
        body: ['LXGW WenKai', 'Source Han Sans SC', 'sans-serif'],
      },
      spacing: {
        '18': '4.5rem',
        '88': '22rem',
        '128': '32rem',
      },
      backdropBlur: {
        xs: '2px',
        sm: '4px',
        md: '8px',
        lg: '12px',
        xl: '16px',
        '2xl': '24px',
        '3xl': '40px',
      },
      animation: {
        'fade-in': 'fadeIn 0.6s ease-out',
        'slide-up': 'slideUp 0.6s ease-out',
        'bounce-gentle': 'bounceGentle 2s ease-in-out infinite',
        'float': 'float 3s ease-in-out infinite',
        'glow': 'glow 2s ease-in-out infinite alternate',
        'shimmer': 'shimmer 2s linear infinite',
        'pulse-gold': 'pulseGold 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'scroll-indicator': 'scrollIndicator 1.5s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideUp: {
          '0%': { transform: 'translateY(20px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        bounceGentle: {
          '0%, 100%': { transform: 'translateY(-5px)' },
          '50%': { transform: 'translateY(5px)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        glow: {
          '0%': { boxShadow: '0 0 5px rgba(212, 175, 55, 0.2), 0 0 10px rgba(212, 175, 55, 0.2), 0 0 15px rgba(212, 175, 55, 0.2)' },
          '100%': { boxShadow: '0 0 10px rgba(212, 175, 55, 0.4), 0 0 20px rgba(212, 175, 55, 0.4), 0 0 30px rgba(212, 175, 55, 0.4)' },
        },
        shimmer: {
          '0%': { transform: 'translateX(-100%)' },
          '100%': { transform: 'translateX(100%)' },
        },
        pulseGold: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.5' },
        },
        scrollIndicator: {
          '0%': { transform: 'translateY(0)', opacity: '0' },
          '50%': { transform: 'translateY(10px)', opacity: '1' },
          '100%': { transform: 'translateY(20px)', opacity: '0' },
        },
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-gold': 'linear-gradient(135deg, #d4af37, #f9e076, #d4af37)',
        'gradient-deepspace': 'linear-gradient(135deg, #0f172a, #1e293b, #334155)',
        'parchment-texture': 'url("data:image/svg+xml,%3Csvg width="20" height="20" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="%23f8f5f0" fill-opacity="0.1" fill-rule="evenodd"%3E%3Ccircle cx="3" cy="3" r="3"/%3E%3Ccircle cx="13" cy="13" r="3"/%3E%3C/g%3E%3C/svg%3E")',
      },
      typography: {
        DEFAULT: {
          css: {
            maxWidth: 'none',
            color: '#0f172a',
            '[class~="lead"]': {
              color: '#334155',
            },
            a: {
              color: '#d4af37',
              textDecoration: 'none',
              fontWeight: '500',
              transition: 'all 0.3s ease',
              '&:hover': {
                color: '#eab308',
                textDecoration: 'none',
                textShadow: '0 0 8px rgba(212, 175, 55, 0.5)',
              },
            },
            'h1, h2, h3, h4': {
              color: '#0f172a',
              fontWeight: '600',
              fontFamily: 'Source Han Serif SC, serif',
            },
            code: {
              color: '#d4af37',
              backgroundColor: 'rgba(212, 175, 55, 0.1)',
              paddingLeft: '6px',
              paddingRight: '6px',
              paddingTop: '3px',
              paddingBottom: '3px',
              borderRadius: '6px',
              fontSize: '0.875em',
              fontFamily: 'JetBrains Mono, monospace',
              border: '1px solid rgba(212, 175, 55, 0.2)',
            },
            'code::before': {
              content: '""',
            },
            'code::after': {
              content: '""',
            },
            blockquote: {
              borderLeftColor: '#d4af37',
              backgroundColor: 'rgba(248, 245, 240, 0.5)',
              padding: '1rem',
              borderRadius: '0.5rem',
              fontStyle: 'normal',
            },
          },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}

