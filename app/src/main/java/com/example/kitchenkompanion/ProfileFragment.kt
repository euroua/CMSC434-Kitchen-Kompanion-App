package com.example.kitchenkompanion

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.kitchenkompanion.databinding.FragmentProfileBinding
import java.util.Calendar

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val selectedAllergies = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.profileImage.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Notification")
                .setMessage("Warning! Do not click on profile image")
                .setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.birthdayInput.setOnClickListener {
            showDatePicker()
        }

        setupAllergySpinner()
        updateAllergiesDisplay()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                binding.birthdayInput.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun setupAllergySpinner() {
        binding.allergiesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) return // Skip "Select an allergy"

                val selected = parent?.getItemAtPosition(position).toString()
                if (selected == "None") {
                    selectedAllergies.clear()
                    selectedAllergies.add("None")
                } else {
                    selectedAllergies.remove("None")
                    selectedAllergies.remove("Select an allergy")
                    selectedAllergies.add(selected)
                }
                updateAllergiesDisplay()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateAllergiesDisplay() {
        if (selectedAllergies.isEmpty()) {
            binding.selectedAllergiesList.text = getString(R.string.none_selected)
        } else {
            binding.selectedAllergiesList.text = selectedAllergies.joinToString(", ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
