---
layout: useful_material
title: React Tesing with Enzyme
---

1. Library to install
    ```shell
    npm install --save-dev enzyme enzyme-adapter-react-16
    ```

1. Setting up test environment to test `React` components with `enzyme`
    ```javascript
    import { configure } from 'enzyme';
    import Adapter from 'enzyme-adapter-react-16';

    configure({ adapter: new Adapter(), disableLifecycleMethods: true });
    ```

1. First **`red`** test for your component
    ```javascript
    import React from 'react';
    import { shallow } from 'enzyme';

    describe('MyComponent', () => {
      it('should be rendered', () => {
        shallow(<MyComponent />);
      });
    });
    ```

1. Move component rendering to setup method (e.g. `beforeEach`)
    ```javascript
    describe('MyComponent', () => {
      let myComponent;

      beforeEach(() => {
        myComponent = shallow(<MyComponent />);
      });
    });
    ```

1. If your component has controls that wouldn't be changed after re-rendering, move their look up to setup method (e.g. `beforeEach`)
    ```javascript
    describe('MyComponent', () => {
      let myComponent;
      let textInput;
      let submitButton;

      beforeEach(() => {
        myComponent = shallow(<MyComponent />);
        textInput = myComponent.find('input');
        submitButton = myComponent.find('button');
      });
    });
    ```

1. Move event simulation to helper functions, that makes your test more descriptive
    ```javascript
    describe('MyComponent', () => {
      // ... component setup

      const userEntersText = (text) => textInput.simulate('change', { targer: { value: text } });
      const userClicksSubmitButton = () => submitButton.simulate('click');
      const componentLabelText = () => myComponent.find('label').text();

      it('changes label text from default to user input', () => {
        userEntersText('my favourite phrase');
        userClicksSubmitButton();

        expect(componentLabelText()).to.be.eq('my favourite phrase');
      });
    });
    ```

1. If test depends on setting up additional state of your component, move it to inner `describe` function
    ```javascript
    describe('Outer', () => {
      // ... component setup

      const userEntersText = (text) => textInput.simulate('change', { targer: { value: text } });
      const userClicksSubmitButton = () => submitButton.simulate('click');
      const componentLabelText = () => myComponent.find('label').text();

      // ... tests
      describe('Inner', () => {
        beforeEach(() => {
          userEntersText('my favourite phrase');
          userClicksSubmitButton();
        });

        const userCleansText = () => userEntersText('');

        it('resets label text to default when user cleans input and submits form', () => {
          userCleansText();
          userClicksSubmitButton();

          expect(componentLabelText()).to.be.eq('Default label text');
        });
      });
    });
    ```

1. Inject behaviour inside a component using Dependency Injection
    ```javascript
    describe('ToDoList', () => {
      const myPredictableTaskIdGenerator = () => 1;

      beforeEach(() => {
        todoList = shallow(<TodoList idGenerator={myPredictableTaskIdGenerator} />);
      });
    });
    ```

1. If your component has to save state to external storage, provide it using Dependency Injection
    ```javascript
    describe('ToDoList', () => {
      let testStorage;
      beforeEach(() => {
        testStorage = mockGlobalStorage();
        todoList = shallow(<TodoList storage={testStorage} />);
      });

      it('saves its state to storage', () => {
        createTaskWithName('Task #1');
        createTaskWithName('Task #2');
        createTaskWithName('Task #3');

        const remountedTodoList = shallow(<TodoList storage={testStorage} />);

        expect(remountedTodoList.find('li')).to.have.length(3);
      });
    });
    ```

1. Previous test can be refactored to look up child component name (e.g. `Task`) instead of `li` tag.
    ```javascript
    import Task from 'path/to/Task/component';

    describe('ToDoList', () => {
      // ... setup
      it('saves its state to storage', () => {
        createTaskWithName('Task #1');
        createTaskWithName('Task #2');
        createTaskWithName('Task #3');

        const remountedTodoList = shallow(<TodoList storage={testStorage} />);

        expect(remountedTodoList.find(Task)).to.have.length(3);
      });
    });
    ```
