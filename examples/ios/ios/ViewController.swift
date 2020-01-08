//
//  ViewController.swift
//  ios
//
//  Created by Alex Mankov on 09.01.2020.
//  Copyright © 2020 Alex Mankov. All rights reserved.
//

import UIKit
import Foundation
import ios_cubic
class ViewController:UIViewController, UIPickerViewDelegate, UIPickerViewDataSource,UIGestureRecognizerDelegate, CubicManagerDelegate {
        func successRecognize(_ res: Cobaltspeech_Cubic_RecognitionResponse) {
            
            for result in res.results {
                if let first = result.alternatives.first {

                    self.resultLabel.text = first.transcript
                } else {
                     self.resultLabel.text = "No result"
                }
            }
        }
        
        
        
        @IBOutlet weak var modelTextField: UITextField!
        @IBOutlet weak var resultLabel: UILabel!
        
        @IBOutlet weak var recordButton: UIButton!
        let pickerView = UIPickerView()
        var models:[Cobaltspeech_Cubic_Model] = []
        var selectedModel:Cobaltspeech_Cubic_Model?
        let cobolt = CubicManager(url:"demo-cubic.cobaltspeech.com:2727")
        override func viewDidLoad() {
            super.viewDidLoad()
            self.cobolt.delegate = self
            modelTextField.inputView = pickerView
            pickerView.delegate = self
            let toolBar = UIToolbar()
            toolBar.barStyle = UIBarStyle.default
            toolBar.isTranslucent = true
            toolBar.tintColor = UIColor(red: 76/255, green: 217/255, blue: 100/255, alpha: 1)
            toolBar.sizeToFit()
            modelTextField.inputAccessoryView = toolBar
            let doneButton = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.done, target: self, action: #selector(self.donePicker))
            let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
           
            toolBar.setItems([spaceButton, doneButton], animated: false)
            toolBar.isUserInteractionEnabled = true
            
            
            onModelSelect(model: nil)
            self.cobolt.listModels { (models, error) in
                
                if let models = models {
                    self.models = models
                    if let first = models.first {
                        self.onModelSelect(model: first)
                    }
                }
                
                
            }
            
        }
        
        
       @objc func donePicker(sender:UIBarButtonItem){
          let row = self.pickerView.selectedRow(inComponent: 0)
          self.pickerView.selectRow(row, inComponent: 0, animated: false)
          onModelSelect(model: self.models[row])
          self.modelTextField.resignFirstResponder()
        }
        @IBAction func recordClickDown(sender:UIButton)  {
            print("recordClick Down")
            self.recordButton.tintColor = UIColor.red
            self.cobolt.record()
        }
        @IBAction func recordClickUp(sender:UIButton)  {
            print("recordClick Up")
             self.cobolt.stop()
            self.recordButton.tintColor = self.view.tintColor
        }
        
        func numberOfComponents(in pickerView: UIPickerView) -> Int {
            return 1
        }
         
        func pickerView( _ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
            return models.count
        }
         
        func pickerView( _ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
            return models[row].name
        }
         
        func pickerView( _ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
            onModelSelect(model: self.models[row])
        }
        
        func onModelSelect(model:Cobaltspeech_Cubic_Model?) {
            selectedModel = model

            if let model = model {
                self.modelTextField.text = model.name
                self.cobolt.selectedModel = model
            } else {
                self.modelTextField.text = "Not selected"
            }
        }
    }
