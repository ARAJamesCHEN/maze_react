import React from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Button
} from 'react-native';

class MazeModule extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>Move Count:</Text>
        <Button
          onPress={() => {
              Alert.alert('You tapped the button!');
            }}
          title="HELP"
          color="#841584"
        />
      </View>
    )
  }
}
var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  text: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});

AppRegistry.registerComponent('MyReactNativeApp', () => MazeModule);
