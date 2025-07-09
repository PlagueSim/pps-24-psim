package model.plague

import model.plague.TraitCategory.Symptom

object Symptoms:

  final val nausea: Trait = Trait(
    name = "Nausea",
    category = Symptom,
    infectivity = 1.0,
    severity = 1.0,
    cost = 2
  )

  final val coughing: Trait = Trait(
    name = "Coughing",
    category = Symptom,
    infectivity = 3.0,
    severity = 1.0,
    cost = 4
  )

  final val rash: Trait = Trait(
    name = "Rash",
    category = Symptom,
    infectivity = 2.0,
    severity = 1.0,
    cost = 3
  )

  final val insomnia: Trait = Trait(
    name = "Insomnia",
    category = Symptom,
    severity = 3.0,
    cost = 2
  )

  final val cysts: Trait = Trait(
    name = "Cysts",
    category = Symptom,
    infectivity = 2.0,
    severity = 2.0,
    cost = 2
  )

  final val anaemia: Trait = Trait(
    name = "Anaemia",
    category = Symptom,
    infectivity = 1.0,
    severity = 1.0,
    cost = 2
  )

  final val vomiting: Trait = Trait(
    name = "Vomiting",
    category = Symptom,
    infectivity = 3.0,
    severity = 1.0,
    cost = 3,
    prerequisites = Set("Nausea", "Pulmonary Edema", "Diarrhea")
  )

  final val pneumonia: Trait = Trait(
    name = "Pneumonia",
    category = Symptom,
    infectivity = 3.0,
    severity = 2.0,
    cost = 3,
    prerequisites = Set("Coughing", "Pulmonary Edema", "Pulmonary Fibrosis")
  )

  final val sneezing: Trait = Trait(
    name = "Sneezing",
    category = Symptom,
    infectivity = 5.0,
    severity = 1.0,
    cost = 5,
    prerequisites = Set("Coughing", "Fever", "Immune Suppression")
  )

  final val sweating: Trait = Trait(
    name = "Sweating",
    category = Symptom,
    infectivity = 2.0,
    severity = 1.0,
    cost = 3,
    prerequisites = Set("Rash", "Fever", "Skin Lesions")
  )

  final val paranoia: Trait = Trait(
    name = "Paranoia",
    category = Symptom,
    severity = 4.0,
    cost = 4,
    prerequisites = Set("Insomnia", "Inflammation", "Seizures")
  )

  final val hyperSensitivity: Trait = Trait(
    name = "Hyper Sensitivity",
    category = Symptom,
    infectivity = 1.0,
    severity = 2.0,
    cost = 2,
    prerequisites = Set("Cysts", "Inflammation", "Paralysis")
  )

  final val abscesses: Trait = Trait(
    name = "Abscesses",
    category = Symptom,
    infectivity = 4.0,
    severity = 4.0,
    cost = 2,
    prerequisites = Set("Cysts", "Tumors", "Systematic Infection")
  )

  final val hemophilia: Trait = Trait(
    name = "Hemophilia",
    category = Symptom,
    infectivity = 4.0,
    severity = 3.0,
    cost = 3,
    prerequisites = Set("Anaemia", "Tumors", "Internal Hemorrhaging")
  )

  final val pulmonaryEdema: Trait = Trait(
    name = "Pulmonary Edema",
    category = Symptom,
    infectivity = 5.0,
    severity = 4.0,
    lethality = 2.0,
    cost = 7,
    prerequisites = Set("Vomiting", "Pneumonia", "Diarrhea", "Pulmonary Fibrosis")
  )

  final val fever: Trait = Trait(
    name = "Fever",
    category = Symptom,
    infectivity = 4.0,
    severity = 3.0,
    lethality = 3.0,
    cost = 9,
    prerequisites = Set("Sneezing", "Sweating", "Immune Suppression", "Skin Lesions")
  )

  final val inflammation: Trait = Trait(
    name = "Inflammation",
    category = Symptom,
    infectivity = 2.0,
    severity = 2.0,
    lethality = 2.0,
    cost = 5,
    prerequisites = Set("Paranoia", "Hyper Sensitivity", "Seizures", "Paralysis")
  )

  final val tumors: Trait = Trait(
    name = "Tumors",
    category = Symptom,
    infectivity = 2.0,
    lethality = 4.0,
    cost = 1,
    prerequisites = Set("Abscesses", "Hemophilia", "Systematic Infection", "Internal Hemorrhaging")
  )

  final val diarrhea: Trait = Trait(
    name = "Diarrhea",
    category = Symptom,
    infectivity = 6.0,
    severity = 4.0,
    lethality = 1.0,
    cost = 6,
    prerequisites = Set("Vomiting", "Pulmonary Edema", "Dysentery")
  )

  final val pulmonaryFibrosis: Trait = Trait(
    name = "Pulmonary Fibrosis",
    category = Symptom,
    infectivity = 3.0,
    severity = 3.0,
    lethality = 2.0,
    cost = 6,
    prerequisites = Set("Pneumonia", "Pulmonary Edema", "Total Organ Failure")
  )

  final val immuneSuppression: Trait = Trait(
    name = "Immune Suppression",
    category = Symptom,
    infectivity = 2.0,
    severity = 6.0,
    lethality = 4.0,
    cost = 12,
    prerequisites = Set("Sneezing", "Fever", "Total Organ Failure")
  )

  final val skinLesions: Trait = Trait(
    name = "Skin Lesions",
    category = Symptom,
    infectivity = 11.0,
    severity = 4.0,
    cost = 8,
    prerequisites = Set("Sweating", "Fever", "Necrosis")
  )

  final val seizures: Trait = Trait(
    name = "Seizures",
    category = Symptom,
    infectivity = 1.0,
    severity = 7.0,
    lethality = 3.0,
    cost = 4,
    prerequisites = Set("Paranoia", "Inflammation", "Insanity")
  )

  final val paralysis: Trait = Trait(
    name = "Paralysis",
    category = Symptom,
    infectivity = 1.0,
    severity = 5.0,
    lethality = 1.0,
    cost = 10,
    prerequisites = Set("Hyper Sensitivity", "Inflammation", "Coma")
  )

  final val systematicInfection: Trait = Trait(
    name = "Systemic Infection",
    category = Symptom,
    infectivity = 6.0,
    severity = 7.0,
    lethality = 6.0,
    cost = 17,
    prerequisites = Set("Abscesses", "Tumors", "Coma")
  )

  final val internalHemorrhaging: Trait = Trait(
    name = "Internal Hemorrhaging",
    category = Symptom,
    severity = 9.0,
    lethality = 7.0,
    cost = 12,
    prerequisites = Set("Hemophilia", "Tumors", "Hemorrhagic Shock")
  )

  final val dysentery: Trait = Trait(
    name = "Dysentery",
    category = Symptom,
    infectivity = 8.0,
    severity = 15.0,
    lethality = 8.0,
    cost = 19,
    prerequisites = Set("Diarrhea", "Insanity")
  )

  final val totalOrganFailure: Trait = Trait(
    name = "Total Organ Failure",
    category = Symptom,
    severity = 20.0,
    lethality = 25.0,
    cost = 28,
    prerequisites = Set("Pulmonary Fibrosis", "Immune Suppression", "Coma")
  )

  final val necrosis: Trait = Trait(
    name = "Necrosis",
    category = Symptom,
    infectivity = 10.0,
    severity = 20.0,
    lethality = 13.0,
    cost = 27,
    prerequisites = Set("Skin Lesions", "Hemorrhagic Shock")
  )

  final val insanity: Trait = Trait(
    name = "Insanity",
    category = Symptom,
    infectivity = 6.0,
    severity = 15.0,
    cost = 18,
    prerequisites = Set("Seizures", "Dysentery")
  )

  final val coma: Trait = Trait(
    name = "Coma",
    category = Symptom,
    severity = 15.0,
    lethality = 2.0,
    cost = 21,
    prerequisites = Set("Paralysis", "Systematic Infection", "Total Organ Failure")
  )

  final val hemorrhagicShock: Trait = Trait(
    name = "Hemorrhagic Shock",
    category = Symptom,
    severity = 15.0,
    lethality = 15.0,
    cost = 23,
    prerequisites = Set("Internal Hemorrhaging", "Necrosis")
  )

  /**
   * @return A set with all symptoms
   */
  def allBasics: List[Trait] = List(
    nausea, coughing, rash, insomnia, cysts, anaemia,
    vomiting, pneumonia, sneezing, sweating, paranoia, hyperSensitivity, abscesses, hemophilia,
    pulmonaryEdema, fever, inflammation, tumors,
    diarrhea, pulmonaryFibrosis, immuneSuppression, skinLesions, seizures, paralysis, systematicInfection, internalHemorrhaging,
    dysentery, totalOrganFailure, necrosis, insanity, coma, hemorrhagicShock
  )

  /**
   * @return A custom trait with the given parameters
   */
  def custom(name: String,
             category: TraitCategory,
             infectivity: Double,
             severity: Double,
             lethality: Double,
             cost: Int,
             prerequisites: Set[String]): Trait = Trait(
    name = name,
    category = category,
    infectivity = infectivity,
    severity = severity,
    lethality = lethality,
    cost = cost,
    prerequisites = prerequisites
  )
