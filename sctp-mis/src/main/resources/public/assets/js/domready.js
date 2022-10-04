// basic implementation of domready
// TODO: Replace with something like https://gist.githubusercontent.com/nathankleyn/613221/raw/b0e872e5f335d9774bed7b4c905188c0563041f8/domready.js

function domready(fn) {
  if (document.readyState != 'loading'){
    fn();
  } else {
    document.addEventListener('DOMContentLoaded', fn);
  }
}
