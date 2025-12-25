import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import { defineConfig, globalIgnores } from 'eslint/config'

/**
 * ESLint Configuration for CampusCard Frontend
 * 
 * This configuration enforces code quality standards including:
 * - Consistent naming conventions
 * - Function and file complexity limits
 * - Best practices for React hooks
 * - Code formatting rules
 * 
 * Run with: npm run lint
 */
export default defineConfig([
  globalIgnores(['dist']),
  {
    files: ['**/*.{js,jsx}'],
    extends: [
      js.configs.recommended,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parserOptions: {
        ecmaVersion: 'latest',
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    rules: {
      // ====================================================================
      // VARIABLE DECLARATIONS
      // ====================================================================
      
      // Disallow unused variables (except components starting with uppercase)
      'no-unused-vars': ['error', { 
        varsIgnorePattern: '^[A-Z_]',
        argsIgnorePattern: '^_'
      }],
      
      // Prefer const for variables that are never reassigned
      'prefer-const': ['warn', {
        destructuring: 'all'
      }],
      
      // Disallow var, use let or const instead
      'no-var': 'error',
      
      // ====================================================================
      // CODE COMPLEXITY
      // ====================================================================
      
      // Maximum lines per file (warn at 400, error at 600)
      'max-lines': ['warn', {
        max: 400,
        skipBlankLines: true,
        skipComments: true
      }],
      
      // Maximum lines per function (warn at 75)
      'max-lines-per-function': ['warn', {
        max: 75,
        skipBlankLines: true,
        skipComments: true,
        IIFEs: true
      }],
      
      // Cyclomatic complexity limit
      'complexity': ['warn', 15],
      
      // Maximum nested callbacks
      'max-nested-callbacks': ['warn', 4],
      
      // Maximum function parameters
      'max-params': ['warn', 5],
      
      // Maximum depth of nested blocks
      'max-depth': ['warn', 4],
      
      // ====================================================================
      // BEST PRACTICES
      // ====================================================================
      
      // Require === and !== (except for null checks)
      'eqeqeq': ['error', 'smart'],
      
      // Disallow console in production (warn for development)
      'no-console': 'warn',
      
      // Disallow debugger statements
      'no-debugger': 'error',
      
      // Disallow alert, confirm, prompt
      'no-alert': 'warn',
      
      // Disallow eval()
      'no-eval': 'error',
      
      // Disallow implied eval through setTimeout/setInterval strings
      'no-implied-eval': 'error',
      
      // Require default case in switch statements
      'default-case': 'warn',
      
      // Disallow else after return
      'no-else-return': 'warn',
      
      // Disallow empty functions (except arrow functions)
      'no-empty-function': ['warn', {
        allow: ['arrowFunctions']
      }],
      
      // Disallow unnecessary boolean casts
      'no-extra-boolean-cast': 'error',
      
      // ====================================================================
      // STYLE CONSISTENCY
      // ====================================================================
      
      // Require camelCase naming
      'camelcase': ['warn', {
        properties: 'never',
        ignoreDestructuring: true
      }],
      
      // Consistent brace style
      'curly': ['warn', 'all'],
      
      // Require spacing around operators
      'space-infix-ops': 'warn',
      
      // Require space before blocks
      'space-before-blocks': 'warn',
      
      // Consistent spacing inside object braces
      'object-curly-spacing': ['warn', 'always'],
      
      // Consistent spacing inside array brackets
      'array-bracket-spacing': ['warn', 'never'],
      
      // Require semicolons
      'semi': ['warn', 'always'],
      
      // Disallow trailing commas
      'comma-dangle': ['warn', 'never'],
      
      // Consistent quotes (single quotes preferred)
      'quotes': ['warn', 'single', {
        avoidEscape: true,
        allowTemplateLiterals: true
      }],
      
      // ====================================================================
      // REACT SPECIFIC
      // ====================================================================
      
      // Enforce React hooks rules (from plugin)
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn'
    },
  },
])
