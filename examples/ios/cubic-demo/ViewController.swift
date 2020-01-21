//
//  ViewController.swift
//  ios
//
//

import UIKit
import Foundation
import ios_cubic

class ViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UIGestureRecognizerDelegate, CubicManagerDelegate {
    
    private static let CUBIC_URL = "demo-cubic.cobaltspeech.com:2727"
    
    @IBOutlet weak var modelTextField: UITextField!
    @IBOutlet weak var resultLabel: UILabel!
    @IBOutlet weak var recordButton: UIButton!
   
    let pickerView = UIPickerView()
    var models:[Cobaltspeech_Cubic_Model] = []
    var selectedModel:Cobaltspeech_Cubic_Model?
    let cubicManager = CubicManager(url: CUBIC_URL)
        
    fileprivate func addToolbar() {
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
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.cubicManager.delegate = self
        modelTextField.inputView = pickerView
        pickerView.delegate = self
        addToolbar()
        
        onModelSelect(model: nil)
        
        self.cubicManager.listModels { (models, error) in
            if let models = models {
                self.models = models
                if let first = models.first {
                    self.onModelSelect(model: first)
                }
            }
        }
    }
        
        
    @objc func donePicker(sender:UIBarButtonItem) {
        let row = self.pickerView.selectedRow(inComponent: 0)
        self.pickerView.selectRow(row, inComponent: 0, animated: false)
        onModelSelect(model: self.models[row])
        self.modelTextField.resignFirstResponder()
    }
    
    
    
    
    @IBAction func recordClickDown(sender:UIButton)  {
        print("recordClick Down")
        if cubicManager.isAuthorized() {
            self.recordButton.tintColor = UIColor.red
            self.cubicManager.record()
            
        } else {
            self.cubicManager.requestAccess { (granted) in
                 print("recordClick \(granted)")
            }
        }
    }
    
    @IBAction func recordClickUp(sender:UIButton)  {
        print("recordClick Up")
        self.cubicManager.stop()
        self.recordButton.tintColor = self.view.tintColor
    }
    
    func managerDidRecognizeWithResponse(_ res: Cobaltspeech_Cubic_RecognitionResponse) {
        for result in res.results {
            if let first = result.alternatives.first {
                self.resultLabel.text = first.transcript
            } else {
                self.resultLabel.text = "No result"
            }
        }
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
            self.cubicManager.selectedModel = model
        } else {
            self.modelTextField.text = "Not selected"
        }
    }
    }
