//
//  SettingsViewController.swift
//  cubic-demo
//
//

import UIKit
import swift_cubic
protocol SettingsViewControllerDelegate: class {
    
    func settingsViewControllerDidChangeModelType(at index: Int)
    
}

class SettingsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var models: [Cobaltspeech_Cubic_Model]!
    
    var selectedModelIndex: Int?
    
    var delegate: SettingsViewControllerDelegate?
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if models.count > 0, let selectedModelIndex = selectedModelIndex {
            let indexPath = IndexPath(row: selectedModelIndex, section: 0)
            tableView.selectRow(at: indexPath, animated: false, scrollPosition: .top)
            tableView(tableView, didSelectRowAt: indexPath)
        }
    }
    
    
    // MARK: - UITableViewDataSource methods

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return models.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ModelTypeCell", for: indexPath)
        cell.selectionStyle = .none
        cell.textLabel?.text = models[indexPath.row].name
        return cell
    }
    
    // MARK: - UITableViewDeelegate methods
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.accessoryType = .checkmark
        delegate?.settingsViewControllerDidChangeModelType(at: indexPath.row)
    }

    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.accessoryType = .none
    }
    
}
