package com.example.annotation;

import javax.lang.model.element.VariableElement;

/**
 * @author hiphonezhu@gmail.com
 * @version [CompilerAnnotation, 17/6/20 11:05]
 */

public class VariableInfo {

        private int viewId;

        private VariableElement variableElement;

        public VariableElement getVariableElement() {
                return variableElement;
        }

        public void setVariableElement(VariableElement variableElement) {
                this.variableElement = variableElement;
        }

        public int getViewId() {
                return viewId;
        }

        public void setViewId(int viewId) {
                this.viewId = viewId;
        }

}