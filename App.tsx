/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useEffect, useState} from 'react';
import {
  Button,
  FlatList,
  NativeEventEmitter,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  ToastAndroid,
  TouchableOpacity,
  useColorScheme,
  View,
} from 'react-native';

import {Colors, Header} from 'react-native/Libraries/NewAppScreen';
import {NativeModules} from 'react-native';
import TolkaPlayerView from './src/TolkaPlayerView';
import Channel from './src/Models';
const {PlayerModule} = NativeModules;
const eventEmitter = new NativeEventEmitter(PlayerModule);

function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const [channelList, setChannelList] = useState<Channel[]>([]);
  const [channel, setChannel] = useState<Channel | null>(null);

  // const debug = false;
  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  // const onPressOpenPlayer = () => {
  //   console.log('labd');
  //   PlayerModule.openPlayer();
  // };

  useEffect(() => {
    const subscription = eventEmitter.addListener('EventFromAndroid', event => {
      ToastAndroid.showWithGravity(
        event.data,
        ToastAndroid.BOTTOM,
        ToastAndroid.CENTER,
      );
      // console.log('Data from Android:', event.data);
      // Alert.alert(`Data from Android: ${event.data}`);
    });

    const subscription1 = eventEmitter.addListener(
      'ChannelFetchListEvent',
      event => {
        console.log('Data from ChannelFetchListEvent:', event.data);
        let userObj = JSON.parse(event.data);
        console.log('Data from userObj:', userObj);
        setChannelList(userObj);
      },
    );
    return () => {
      subscription.remove();
      subscription1.remove();
    };
  }, []);

  const getChannelListOrScan = () => {
    PlayerModule.scan(true);
  };

  const onPressItem = (item: Channel, index: number) => {
    console.log('onPressItem', JSON.stringify(item));
    setChannel(item);
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Header />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <TolkaPlayerView
            style={styles.customView}
            channel={channel}
          />
          <Button title="Scan Channels" onPress={getChannelListOrScan} />
          <FlatList
            data={channelList}
            style={{flex: 1}}
            renderItem={({item, index}) => {
              return (
                <TouchableOpacity
                  onPress={() => {
                    onPressItem(item, index);
                  }}>
                  <View style={styles.item}>
                    <Text style={styles.title}>{item.name}</Text>
                  </View>
                </TouchableOpacity>
              );
            }}
            keyExtractor={item => item.majorNum+"-"+item.minorNum+" "+item.name}
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
  customView: {
    flex: 1,
    backgroundColor: 'red',
    height: 200,
  },
  item: {
    padding: 2,
    marginVertical: 4,
    marginHorizontal: 16,
  },
  title: {
    fontSize: 16,
  },
});

export default App;
