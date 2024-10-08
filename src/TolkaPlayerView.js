// MyCustomView.js
import {requireNativeComponent} from 'react-native';
import PropTypes from 'prop-types';

const TolkaPlayerView = requireNativeComponent('TolkaPlayerView');

TolkaPlayerView.propTypes = {
  exampleProp: PropTypes.string,
};

export default TolkaPlayerView;
